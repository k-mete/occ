package org.agora.occ.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing a single checkpoint entry recorded during a trip.
 *
 * <p>
 * Either {@code jplId} or {@code stationId} should be set to indicate the type
 * of checkpoint that was passed.
 * </p>
 */
@Entity
@Table(name = "trip_progress")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripProgressEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "trip_id", nullable = false)
    private UUID tripId;

    @ManyToOne
    @JoinColumn(name = "jpl_id")
    private JplEntity jpl;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private StationEntity station;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;
}
