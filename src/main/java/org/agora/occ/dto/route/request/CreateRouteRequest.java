package org.agora.occ.dto.route.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.agora.occ.enums.TransportCategory;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateRouteRequest {

    @JsonProperty("routeCode")
    @NotBlank(message = "Route code is required")
    private String routeCode;

    @JsonProperty("routeDistance")
    @Min(value = 0, message = "Route distance must be non-negative")
    private Double routeDistance;

    @JsonProperty("category")
    @NotNull(message = "Category is required")
    private TransportCategory category;

    @JsonProperty("isActive")
    @NotNull(message = "Active status is required")
    private Boolean isActive;

    @JsonProperty("fromStationName")
    private String fromStationName;

    @JsonProperty("toStationName")
    private String toStationName;

    @JsonProperty("segments")
    @NotEmpty(message = "At least one segment is required")
    private List<@Valid SegmentItem> segments;
}
