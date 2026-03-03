package org.agora.occ.dto.jpl.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.agora.occ.enums.ActiveStatus;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CreateJplRequest {

    @NotBlank(message = "JPL name is required")
    private String jplName;

    private String jplAddress;

    @NotNull(message = "JPL status is required")
    private ActiveStatus jplStatus;

    @NotNull(message = "Station ID is required")
    private UUID stationId;

    private Double jplLatitude;

    private Double jplLongitude;

    private Integer heading;
}
