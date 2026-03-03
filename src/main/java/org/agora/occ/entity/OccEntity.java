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

@Entity
@Table(name = "occ")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OccEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "occ_name", nullable = false)
    private String occName;

    @Column(name = "occ_latitude", nullable = false)
    private Double occLatitude;

    @Column(name = "occ_longitude", nullable = false)
    private Double occLongitude;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
