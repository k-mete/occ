package org.agora.occ.dto.health;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Aggregate tally wrapper summarizing all tracked WebSocket connections.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class HealthSummaryResponse {
    private int jplOnline;
    private int jplOffline;
    private int trainOnline;
    private int trainOffline;
    private int stationOnline;
    private int stationOffline;
    private int totalOnline;
    private int totalOffline;
    private Instant checkedAt;
}
