package org.agora.occ.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.agora.occ.enums.ActiveStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "route_segment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteSegmentEntity extends PanacheEntityBase {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "route_segment_code", nullable = false)
    private String routeSegmentCode;

    @ManyToOne
    @JoinColumn(name = "from_station_id")
    private StationEntity fromStation;

    @ManyToOne
    @JoinColumn(name = "to_station_id")
    private StationEntity toStation;

    @Column(name = "route_duration")
    private Integer routeDuration;

    @Column(name = "route_distance")
    private Double routeDistance;

    @Enumerated(EnumType.STRING)
    @Column(name = "route_status", nullable = false)
    private ActiveStatus routeStatus;

    @ManyToMany
    @JoinTable(name = "route_segment_jpl", joinColumns = @JoinColumn(name = "route_segment_id"), inverseJoinColumns = @JoinColumn(name = "jpl_id"))
    private List<JplEntity> jpls;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
