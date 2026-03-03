package org.agora.occ.dto.trip.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/**
 * Request payload for creating a new train trip.
 */
@Data
public class CreateTripRequest {

    @NotNull(message = "trainId is required")
    private UUID trainId;

    @NotNull(message = "routeId is required")
    private UUID routeId;

    @NotNull(message = "isFlow is required")
    private Boolean isFlow;

    @NotNull(message = "startTime is required")
    private Instant startTime;

    private Instant endTime;
}
