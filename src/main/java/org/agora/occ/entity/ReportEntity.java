package org.agora.occ.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import jakarta.persistence.ForeignKey;
import jakarta.persistence.ConstraintMode;
import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity representing a field report uploaded from a train or JPL.
 * Images are stored in MinIO; their paths are persisted as a comma-separated
 * string in {@code file_paths}.
 */
@Entity
@Table(name = "reports")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    /** Report category: LOCO or JPL. */
    @Column(name = "type")
    private String type;

    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** Comma-separated MinIO object URLs for the uploaded images. */
    @Column(name = "file_paths", columnDefinition = "TEXT")
    private String filePaths;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "train_id")
    private UUID trainId;

    @ManyToOne
    @JoinColumn(name = "train_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    private TrainEntity train;

    @Column(name = "jpl_id")
    private UUID jplId;

    @ManyToOne
    @JoinColumn(name = "jpl_id", insertable = false, updatable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @NotFound(action = NotFoundAction.IGNORE)
    private JplEntity jpl;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;
}
