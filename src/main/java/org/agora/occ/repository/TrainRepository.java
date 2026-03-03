package org.agora.occ.repository;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.entity.TrainEntity;
import org.agora.occ.enums.ActiveStatus;
import org.agora.occ.enums.TransportCategory;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TrainRepository implements PanacheRepositoryBase<TrainEntity, UUID> {

    public List<TrainEntity> findFiltered(TransportCategory category, ActiveStatus status, int page, int size) {
        PanacheQuery<TrainEntity> query = buildFilteredQuery(category, status);
        if (size > 0) {
            query.page(Page.of(page, size));
        }
        return query.list();
    }

    public long countFiltered(TransportCategory category, ActiveStatus status) {
        return buildFilteredQuery(category, status).count();
    }

    private PanacheQuery<TrainEntity> buildFilteredQuery(TransportCategory category, ActiveStatus status) {
        StringBuilder query = new StringBuilder("1=1");
        Parameters params = new Parameters();

        if (category != null) {
            query.append(" and category = :category");
            params.and("category", category);
        }

        if (status != null) {
            query.append(" and trainStatus = :status");
            params.and("status", status);
        }

        return find(query.toString(), params);
    }
}
