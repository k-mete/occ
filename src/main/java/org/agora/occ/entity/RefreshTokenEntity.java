package org.agora.occ.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing a long-lived JWT refresh token mapping to
 * "refresh_token".
 */
@Entity
@Table(name = "\"refresh_token\"")
@Getter
@Setter
public class RefreshTokenEntity {

    @Id
    @Column(name = "\"tokenId\"")
    private UUID tokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"userId\"", nullable = false)
    private UserEntity user;

    @Column(name = "\"tokenStr\"", nullable = false, unique = true, length = 255)
    private String tokenStr;

    @Column(name = "\"expiresAt\"", nullable = false)
    private Instant expiresAt;

    @Column(name = "\"revoked\"", nullable = false)
    private boolean revoked = false;

    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (tokenId == null) {
            tokenId = UUID.randomUUID();
        }
        createdAt = Instant.now();
    }
}
