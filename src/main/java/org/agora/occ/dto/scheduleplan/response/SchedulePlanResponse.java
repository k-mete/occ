package org.agora.occ.dto.scheduleplan.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.agora.occ.enums.Directions;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class SchedulePlanResponse {

    @JsonProperty("type")
    private String type; // JPL_SCHEDULE or STATION_SCHEDULE

    @JsonProperty("planId")
    private UUID planId;

    @JsonProperty("trainId")
    private UUID trainId;

    @JsonProperty("trainCode")
    private String trainCode;

    @JsonProperty("trainName")
    private String trainName;

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

    @JsonProperty("createdAt")
    private Instant createdAt;

    @JsonProperty("updatedAt")
    private Instant updatedAt;
}
