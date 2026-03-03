package org.agora.occ.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.agora.occ.enums.TransportCategory;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "route")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteEntity extends PanacheEntityBase {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "route_code", nullable = false, unique = true)
    private String routeCode;

    @Column(name = "route_distance")
    private Double routeDistance;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private TransportCategory category;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "from_station_name")
    private String fromStationName;

    @Column(name = "to_station_name")
    private String toStationName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
