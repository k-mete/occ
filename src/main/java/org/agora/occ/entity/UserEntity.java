package org.agora.occ.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.agora.occ.enums.UserRole;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing an authenticated user account mapping to the "user"
 * table.
 */
@Entity
@Table(name = "\"user\"")
@Getter
@Setter
public class UserEntity {

    @Id
    @Column(name = "\"userId\"")
    private UUID userId;

    @Column(name = "\"nrp\"", nullable = false, unique = true, length = 50)
    private String nrp;

    @Column(name = "\"fullName\"", nullable = false, length = 255)
    private String fullName;

    @Column(name = "\"password\"", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"role\"", nullable = false, length = 50)
    private UserRole role;

    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "\"updatedAt\"")
    private Instant updatedAt;

    @Column(name = "\"createdBy\"")
    private UUID createdBy;

    @Column(name = "\"updatedBy\"")
    private UUID updatedBy;

    @PrePersist
    protected void onCreate() {
        if (userId == null) {
            userId = UUID.randomUUID();
        }
        createdAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
