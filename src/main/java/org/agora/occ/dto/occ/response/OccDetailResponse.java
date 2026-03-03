package org.agora.occ.dto.occ.response;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.agora.occ.dto.station.response.StationResponse;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class OccDetailResponse {

    private UUID id;
    private String occName;
    private Double occLatitude;
    private Double occLongitude;
    private Instant createdAt;
    private Instant updatedAt;
    private List<StationResponse> stations;
}
