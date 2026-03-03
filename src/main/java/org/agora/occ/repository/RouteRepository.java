package org.agora.occ.repository;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.entity.RouteEntity;
import org.agora.occ.enums.TransportCategory;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class RouteRepository implements PanacheRepositoryBase<RouteEntity, UUID> {

    public List<RouteEntity> findFiltered(TransportCategory category, Boolean isActive, int page, int size) {
        PanacheQuery<RouteEntity> query = buildFilteredQuery(category, isActive);
        if (size > 0) {
            query.page(Page.of(page, size));
        }
        return query.list();
    }

    public long countFiltered(TransportCategory category, Boolean isActive) {
        return buildFilteredQuery(category, isActive).count();
    }

    private PanacheQuery<RouteEntity> buildFilteredQuery(TransportCategory category, Boolean isActive) {
        StringBuilder query = new StringBuilder("1=1");
        Parameters params = new Parameters();

        if (category != null) {
            query.append(" and category = :category");
            params.and("category", category);
        }

        if (isActive != null) {
            query.append(" and isActive = :isActive");
            params.and("isActive", isActive);
        }

        return find(query.toString(), params);
    }
}
