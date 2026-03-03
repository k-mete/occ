package org.agora.occ.repository;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.entity.WarningEntity;
import org.agora.occ.enums.WarningLevel;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Repository for managing {@link WarningEntity} data in PostgreSQL.
 */
@ApplicationScoped
public class WarningRepository implements PanacheRepositoryBase<WarningEntity, UUID> {

    /**
     * Finds warnings based on dynamic optional filters, sorted ascending or
     * descending.
     * In this implementation, warnings are returned sorted by descending timestamp.
     *
     * @param jplId        optional JPL ID filter
     * @param trainId      optional Train ID filter
     * @param warningLevel optional warning severity filter
     * @param dateFrom     optional start time of the alert
     * @param dateTo       optional end time of the alert
     * @return a {@link PanacheQuery} ready for pagination or direct fetching
     */
    public PanacheQuery<WarningEntity> findFiltered(
            UUID jplId,
            UUID trainId,
            WarningLevel warningLevel,
            Instant dateFrom,
            Instant dateTo) {

        StringBuilder query = new StringBuilder("1 = 1");
        Map<String, Object> params = new HashMap<>();

        if (jplId != null) {
            query.append(" AND jplId = :jplId");
            params.put("jplId", jplId);
        }
        if (trainId != null) {
            query.append(" AND trainId = :trainId");
            params.put("trainId", trainId);
        }
        if (warningLevel != null) {
            query.append(" AND warningLevel = :warningLevel");
            params.put("warningLevel", warningLevel);
        }
        if (dateFrom != null) {
            query.append(" AND alertTimestamp >= :dateFrom");
            params.put("dateFrom", dateFrom);
        }
        if (dateTo != null) {
            query.append(" AND alertTimestamp <= :dateTo");
            params.put("dateTo", dateTo);
        }

        query.append(" ORDER BY alertTimestamp DESC");

        return find(query.toString(), params);
    }
}
