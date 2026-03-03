package org.agora.occ.dto.station.response;

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
public class StationResponse {

    private UUID id;
    private String stationName;
    private String stationCode;
    private String stationAddress;
    private Double stationLatitude;
    private Double stationLongitude;
    private ActiveStatus stationStatus;
    private Integer heading;
    private UUID occId;
    private String occName;
    private Integer stationIndex;
    private Instant createdAt;
    private Instant updatedAt;
}
