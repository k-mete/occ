package org.agora.occ.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.entity.RouteSegmentOrderEntity;
import org.agora.occ.entity.RouteSegmentOrderEntity.RouteSegmentOrderId;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class RouteSegmentOrderRepository
        implements PanacheRepositoryBase<RouteSegmentOrderEntity, RouteSegmentOrderId> {

    public List<RouteSegmentOrderEntity> findByRouteIdOrderedByIndex(UUID routeId) {
        return find("route.id = ?1 order by segmentIndex asc", routeId).list();
    }

    public long deleteByRouteId(UUID routeId) {
        return delete("route.id", routeId);
    }
}
