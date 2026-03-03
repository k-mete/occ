package org.agora.occ.repository;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.entity.StationEntity;
import org.agora.occ.enums.ActiveStatus;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class StationRepository implements PanacheRepositoryBase<StationEntity, UUID> {

    public List<StationEntity> findByOccId(UUID occId) {
        return find("occ.id", occId).list();
    }

    public java.util.Optional<StationEntity> findByStationName(String stationName) {
        return find("stationName", stationName).firstResultOptional();
    }

    public List<StationEntity> findFiltered(UUID occId, ActiveStatus status, int page, int size) {
        PanacheQuery<StationEntity> query = buildFilteredQuery(occId, status);
        if (size > 0) {
            query.page(Page.of(page, size));
        }
        return query.list();
    }

    public long countFiltered(UUID occId, ActiveStatus status) {
        return buildFilteredQuery(occId, status).count();
    }

    private PanacheQuery<StationEntity> buildFilteredQuery(UUID occId, ActiveStatus status) {
        StringBuilder query = new StringBuilder("1=1");
        Parameters params = new Parameters();

        if (occId != null) {
            query.append(" and occ.id = :occId");
            params.and("occId", occId);
        }

        if (status != null) {
            query.append(" and stationStatus = :status");
            params.and("status", status);
        }

        return find(query.toString(), params);
    }
}
