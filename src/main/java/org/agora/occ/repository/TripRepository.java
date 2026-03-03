package org.agora.occ.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.entity.TripEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link TripEntity} providing dynamic filtered queries.
 */
@ApplicationScoped
public class TripRepository implements PanacheRepositoryBase<TripEntity, UUID> {

    /**
     * Finds trips matching the provided optional filter parameters, ordered by
     * {@code startTime} descending.
     *
     * @param trainId optional filter by train UUID
     * @param routeId optional filter by route UUID
     * @param isFlow  optional filter by trip direction
     * @param from    optional lower bound for startTime (inclusive)
     * @param to      optional upper bound for startTime (inclusive)
     * @param page    zero-based page index
     * @param size    items per page
     * @return filtered and paginated list of trip entities
     */
    public List<TripEntity> findFiltered(UUID trainId, UUID routeId, Boolean isFlow,
            Instant from, Instant to, int page, int size) {
        StringBuilder query = new StringBuilder("1=1");
        List<Object> params = new ArrayList<>();
        int paramIndex = 1;

        if (trainId != null) {
            query.append(" AND trainId = ?").append(paramIndex++);
            params.add(trainId);
        }
        if (routeId != null) {
            query.append(" AND routeId = ?").append(paramIndex++);
            params.add(routeId);
        }
        if (isFlow != null) {
            query.append(" AND isFlow = ?").append(paramIndex++);
            params.add(isFlow);
        }
        if (from != null) {
            query.append(" AND startTime >= ?").append(paramIndex++);
            params.add(from);
        }
        if (to != null) {
            query.append(" AND startTime <= ?").append(paramIndex);
            params.add(to);
        }

        query.append(" ORDER BY startTime DESC");

        return find(query.toString(), params.toArray())
                .page(page, size)
                .list();
    }

    /**
     * Counts the total number of trips matching the provided filters.
     *
     * @param trainId optional filter by train UUID
     * @param routeId optional filter by route UUID
     * @param isFlow  optional filter by trip direction
     * @param from    optional lower bound for startTime (inclusive)
     * @param to      optional upper bound for startTime (inclusive)
     * @return total count of matching trips
     */
    public long countFiltered(UUID trainId, UUID routeId, Boolean isFlow,
            Instant from, Instant to) {
        StringBuilder query = new StringBuilder("1=1");
        List<Object> params = new ArrayList<>();
        int paramIndex = 1;

        if (trainId != null) {
            query.append(" AND trainId = ?").append(paramIndex++);
            params.add(trainId);
        }
        if (routeId != null) {
            query.append(" AND routeId = ?").append(paramIndex++);
            params.add(routeId);
        }
        if (isFlow != null) {
            query.append(" AND isFlow = ?").append(paramIndex++);
            params.add(isFlow);
        }
        if (from != null) {
            query.append(" AND startTime >= ?").append(paramIndex++);
            params.add(from);
        }
        if (to != null) {
            query.append(" AND startTime <= ?").append(paramIndex);
            params.add(to);
        }

        return count(query.toString(), params.toArray());
    }

    /**
     * Finds all trips for the given train, ordered by startTime descending.
     *
     * @param trainId the train UUID
     * @return list of trip entities for the specified train
     */
    public List<TripEntity> findByTrainId(UUID trainId) {
        return find("trainId = ?1 ORDER BY startTime DESC", trainId).list();
    }
}
