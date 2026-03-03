package org.agora.occ.dto.trip.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/**
 * Request payload for recording a trip progress checkpoint.
 *
 * <p>
 * Either {@code jplId} or {@code stationId} should be provided.
 * {@code timestamp} defaults to the current server time if omitted.
 * </p>
 */
@Data
public class RecordProgressRequest {

    @NotNull(message = "tripId is required")
    private UUID tripId;

    private UUID jplId;

    private UUID stationId;

    private Instant timestamp;
}
