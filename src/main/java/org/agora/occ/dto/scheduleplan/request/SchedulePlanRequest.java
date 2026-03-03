package org.agora.occ.dto.scheduleplan.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.agora.occ.enums.Directions;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
public class SchedulePlanRequest {

    @JsonProperty("trainId")
    @NotNull(message = "Train ID is required")
    private UUID trainId;

    // JPL Schedule fields
    @JsonProperty("jplId")
    private UUID jplId;

    @JsonProperty("estimatedPassTime")
    private Instant estimatedPassTime;

    @JsonProperty("direction")
    private Directions direction;

    // Station Schedule fields
    @JsonProperty("stationId")
    private UUID stationId;

    @JsonProperty("arrivalPlan")
    private Instant arrivalPlan;

    @JsonProperty("departurePlan")
    private Instant departurePlan;

    @JsonProperty("description")
    private String description;
}
