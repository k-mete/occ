package org.agora.occ.dto.route.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.agora.occ.enums.ActiveStatus;

import java.util.List;

@Data
@NoArgsConstructor
public class SegmentItem {

    @JsonProperty("routeSegmentCode")
    @NotBlank(message = "Segment code is required")
    private String routeSegmentCode;

    @JsonProperty("fromStationId")
    @NotBlank(message = "From station name is required")
    private String fromStationId; // Using fromStationId key to match frontend, but storing name

    @JsonProperty("toStationId")
    @NotBlank(message = "To station name is required")
    private String toStationId; // Using toStationId key to match frontend, but storing name

    @JsonProperty("routeDuration")
    @Min(value = 0, message = "Route duration must be non-negative")
    private Integer routeDuration;

    @JsonProperty("routeDistance")
    @Min(value = 0, message = "Route distance must be non-negative")
    private Double routeDistance;

    @JsonProperty("routeStatus")
    @NotNull(message = "Route status is required")
    private ActiveStatus routeStatus;

    @JsonProperty("jpls")
    private List<JplRefItem> jpls;
}
