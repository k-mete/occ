package org.agora.occ.dto.route.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.agora.occ.enums.TransportCategory;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class RouteDetailResponse {

    @JsonProperty("routeId")
    private UUID routeId;

    @JsonProperty("routeCode")
    private String routeCode;

    @JsonProperty("routeDistance")
    private Double routeDistance;

    @JsonProperty("category")
    private TransportCategory category;

    @JsonProperty("isActive")
    private Boolean isActive;

    @JsonProperty("fromStationName")
    private String fromStationName;

    @JsonProperty("toStationName")
    private String toStationName;

    @JsonProperty("createdAt")
    private Instant createdAt;

    @JsonProperty("updatedAt")
    private Instant updatedAt;

    @JsonProperty("segments")
    private List<RouteSegmentDetailResponse> segments;
}
