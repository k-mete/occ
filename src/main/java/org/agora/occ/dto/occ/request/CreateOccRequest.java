package org.agora.occ.dto.occ.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateOccRequest {

    @NotBlank(message = "OCC name is required")
    private String occName;

    @NotNull(message = "OCC latitude is required")
    private Double occLatitude;

    @NotNull(message = "OCC longitude is required")
    private Double occLongitude;
}
