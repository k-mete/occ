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
import lombok.Getter;
import lombok.Setter;
import org.agora.occ.enums.Directions;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "jpl_schedule_plan")
@Getter
@Setter
public class JplSchedulePlanEntity extends PanacheEntityBase {

    @Id
    @Column(name = "plan_id")
    private UUID planId;

    @ManyToOne
    @JoinColumn(name = "train_id", nullable = false)
    private TrainEntity train;

    @ManyToOne
    @JoinColumn(name = "jpl_id", nullable = false)
    private JplEntity jpl;

    @Column(name = "estimated_pass_time", nullable = false)
    private Instant estimatedPassTime;

    @Column(name = "direction", nullable = false)
    @Enumerated(EnumType.STRING)
    private Directions direction;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
