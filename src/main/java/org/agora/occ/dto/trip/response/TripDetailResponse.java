package org.agora.occ.dto.trip.response;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Enriched trip response including all recorded progress checkpoints.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class TripDetailResponse {

    private UUID id;
    private UUID trainId;
    private UUID routeId;
    private Boolean isFlow;
    private Instant startTime;
    private Instant endTime;
    private Instant createdAt;
    private List<TripProgressResponse> progress;
}
