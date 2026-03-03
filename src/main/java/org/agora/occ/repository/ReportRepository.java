package org.agora.occ.repository;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.entity.ReportEntity;

import java.util.UUID;

/**
 * Panache repository for {@link ReportEntity}.
 */
@ApplicationScoped
public class ReportRepository implements PanacheRepositoryBase<ReportEntity, UUID> {

    /**
     * Finds a report by matching a filename substring inside the comma-separated
     * {@code filePaths} column.
     *
     * @param fileName partial filename to search for
     * @return the first matching entity, or {@code null}
     */
    public ReportEntity findByFileName(String fileName) {
        return find("filePaths LIKE ?1", "%" + fileName + "%").firstResult();
    }

    /**
     * Returns a dynamic filtered query for reports, supporting optional filter
     * parameters.
     *
     * @param type      report type filter (e.g. "LOCO" or "JPL")
     * @param jplName   partial JPL name filter (joined)
     * @param trainName partial train name filter (joined)
     * @param date      date string filter on {@code createdAt}
     * @param jplId     exact JPL UUID filter
     * @param trainId   exact Train UUID filter
     * @return a Panache query that can be paged or listed
     */
    public PanacheQuery<ReportEntity> findReports(
            String type, String jplName, String trainName,
            String date, UUID jplId, UUID trainId) {

        StringBuilder query = new StringBuilder(
                "SELECT r FROM ReportEntity r LEFT JOIN r.jpl j LEFT JOIN r.train t WHERE 1=1");

        if (type != null && !type.isBlank()) {
            query.append(" AND r.type = '").append(type).append("'");
        }
        if (jplId != null) {
            query.append(" AND r.jplId = '").append(jplId).append("'");
        }
        if (trainId != null) {
            query.append(" AND r.trainId = '").append(trainId).append("'");
        }
        if (jplName != null && !jplName.isBlank()) {
            query.append(" AND j.jplName LIKE '%").append(jplName.replace("'", "''")).append("%'");
        }
        if (trainName != null && !trainName.isBlank()) {
            query.append(" AND t.trainName LIKE '%").append(trainName.replace("'", "''")).append("%'");
        }
        if (date != null && !date.isBlank()) {
            query.append(" AND CAST(r.createdAt AS date) = '").append(date).append("'");
        }
        query.append(" ORDER BY r.createdAt DESC");

        return find(query.toString());
    }
}
