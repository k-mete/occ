package org.agora.occ.dto.health;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.agora.occ.enums.ConnectionStatus;

import java.util.UUID;

/**
 * Lightweight health response element representing a single entity in a list.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class HealthItemResponse {
    private UUID id;
    private ConnectionStatus status;
}
