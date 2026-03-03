package org.agora.occ.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing a train trip on a route.
 */
@Entity
@Table(name = "trip")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "train_id", nullable = false)
    private UUID trainId;

    @Column(name = "route_id", nullable = false)
    private UUID routeId;

    /**
     * Indicates the direction of the trip.
     * {@code true} = departure flow, {@code false} = return flow.
     */
    @Column(name = "is_flow", nullable = false)
    private Boolean isFlow;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
