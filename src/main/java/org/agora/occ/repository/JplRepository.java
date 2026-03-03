package org.agora.occ.repository;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.entity.JplEntity;
import org.agora.occ.enums.ActiveStatus;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class JplRepository implements PanacheRepositoryBase<JplEntity, UUID> {

    public List<JplEntity> findByStationId(UUID stationId) {
        return find("station.id", stationId).list();
    }

    public List<JplEntity> findFiltered(UUID stationId, UUID occId, ActiveStatus status, int page, int size) {
        PanacheQuery<JplEntity> query = buildFilteredQuery(stationId, occId, status);
        if (size > 0) {
            query.page(Page.of(page, size));
        }
        return query.list();
    }

    public long countFiltered(UUID stationId, UUID occId, ActiveStatus status) {
        return buildFilteredQuery(stationId, occId, status).count();
    }

    private PanacheQuery<JplEntity> buildFilteredQuery(UUID stationId, UUID occId, ActiveStatus status) {
        StringBuilder query = new StringBuilder("1=1");
        Parameters params = new Parameters();

        if (stationId != null) {
            query.append(" and station.id = :stationId");
            params.and("stationId", stationId);
        }

        if (occId != null) {
            query.append(" and station.occ.id = :occId");
            params.and("occId", occId);
        }

        if (status != null) {
            query.append(" and jplStatus = :status");
            params.and("status", status);
        }

        return find(query.toString(), params);
    }
}
