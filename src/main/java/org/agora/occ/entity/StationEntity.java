package org.agora.occ.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.agora.occ.enums.ActiveStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "station")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "station_name", nullable = false)
    private String stationName;

    @Column(name = "station_code")
    private String stationCode;

    @Column(name = "station_address", columnDefinition = "TEXT")
    private String stationAddress;

    @Column(name = "station_latitude", nullable = false)
    private Double stationLatitude;

    @Column(name = "station_longitude", nullable = false)
    private Double stationLongitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "station_status", nullable = false)
    private ActiveStatus stationStatus;

    @Column(name = "heading")
    private Integer heading;

    @ManyToOne
    @JoinColumn(name = "occ_id", nullable = false)
    private OccEntity occ;

    @Column(name = "station_index")
    private Integer stationIndex;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
