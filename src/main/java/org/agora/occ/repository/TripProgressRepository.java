package org.agora.occ.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.entity.TripProgressEntity;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link TripProgressEntity} providing trip checkpoint queries.
 */
@ApplicationScoped
public class TripProgressRepository implements PanacheRepositoryBase<TripProgressEntity, UUID> {

    /**
     * Finds all progress checkpoints for a given trip, ordered by timestamp
     * ascending.
     *
     * @param tripId the trip UUID
     * @return ordered list of checkpoints for the specified trip
     */
    public List<TripProgressEntity> findByTripId(UUID tripId) {
        return find("tripId = ?1 ORDER BY timestamp ASC", tripId).list();
    }
}
