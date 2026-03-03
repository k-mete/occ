package org.agora.occ.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.entity.OccEntity;

import java.util.UUID;

@ApplicationScoped
public class OccRepository implements PanacheRepositoryBase<OccEntity, UUID> {
}
