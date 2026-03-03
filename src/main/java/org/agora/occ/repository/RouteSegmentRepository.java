package org.agora.occ.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.entity.RouteSegmentEntity;

import java.util.UUID;

@ApplicationScoped
public class RouteSegmentRepository implements PanacheRepositoryBase<RouteSegmentEntity, UUID> {
}
