package org.agora.occ.dto.health;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.agora.occ.enums.ConnectionStatus;
import org.agora.occ.enums.HealthCategory;

import java.time.Instant;
import java.util.UUID;

/**
 * Detailed health response for a specific entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class HealthDetailResponse {
    private UUID id;
    private HealthCategory category;
    private ConnectionStatus status;
    private int activeConnections;
    private Instant checkedAt;
}
