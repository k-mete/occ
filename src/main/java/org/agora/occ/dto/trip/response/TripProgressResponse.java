package org.agora.occ.dto.trip.response;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO representing a single trip progress checkpoint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class TripProgressResponse {

    private UUID id;
    private UUID tripId;
    private UUID jplId;
    private UUID stationId;
    private Instant timestamp;
}
