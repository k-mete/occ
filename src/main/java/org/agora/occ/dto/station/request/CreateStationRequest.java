package org.agora.occ.dto.station.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.agora.occ.enums.ActiveStatus;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CreateStationRequest {

    @NotBlank(message = "Station name is required")
    private String stationName;

    private String stationCode;

    private String stationAddress;

    @NotNull(message = "Station latitude is required")
    private Double stationLatitude;

    @NotNull(message = "Station longitude is required")
    private Double stationLongitude;

    @NotNull(message = "Station status is required")
    private ActiveStatus stationStatus;

    private Integer heading;

    @NotNull(message = "OCC ID is required")
    private UUID occId;

    private Integer stationIndex;
}
