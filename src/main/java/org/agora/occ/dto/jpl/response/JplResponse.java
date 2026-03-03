package org.agora.occ.dto.jpl.response;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.agora.occ.enums.ActiveStatus;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class JplResponse {
    private UUID id;
    private String jplName;
    private String jplAddress;
    private ActiveStatus jplStatus;
    private UUID stationId;
    private String stationName;
    private Double jplLatitude;
    private Double jplLongitude;
    private Integer heading;
    private Instant createdAt;
    private Instant updatedAt;
}
