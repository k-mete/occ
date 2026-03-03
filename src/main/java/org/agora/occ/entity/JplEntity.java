package org.agora.occ.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
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
@Table(name = "jpl")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JplEntity extends PanacheEntityBase {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "jpl_name", nullable = false)
    private String jplName;

    @Column(name = "jpl_address", columnDefinition = "TEXT")
    private String jplAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "jpl_status", nullable = false)
    private ActiveStatus jplStatus;

    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private StationEntity station;

    @Column(name = "jpl_latitude")
    private Double jplLatitude;

    @Column(name = "jpl_longitude")
    private Double jplLongitude;

    @Column(name = "heading", nullable = false)
    private Integer heading;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
