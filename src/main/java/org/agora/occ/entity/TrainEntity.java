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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.agora.occ.enums.ActiveStatus;
import org.agora.occ.enums.TransportCategory;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "train")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainEntity extends PanacheEntityBase {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "train_name", nullable = false)
    private String trainName;

    @Column(name = "train_code")
    private String trainCode;

    @Column(name = "train_network_ip")
    private String trainNetworkIp;

    @Enumerated(EnumType.STRING)
    @Column(name = "train_status", nullable = false)
    private ActiveStatus trainStatus;

    @Column(name = "train_online", nullable = false)
    private Boolean trainOnline;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private TransportCategory category;

    @Column(name = "train_last_known_latitude")
    private Double trainLastKnownLatitude;

    @Column(name = "train_last_known_longitude")
    private Double trainLastKnownLongitude;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private RouteEntity route;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
