package org.agora.occ.dto.train.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.agora.occ.enums.ActiveStatus;
import org.agora.occ.enums.TransportCategory;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CreateTrainRequest {

    @NotBlank(message = "Train name is required")
    private String trainName;

    private String trainCode;

    private String trainNetworkIp;

    @NotNull(message = "Train status is required")
    private ActiveStatus trainStatus;

    @NotNull(message = "Category is required")
    private TransportCategory category;

    private UUID routeId;
}
