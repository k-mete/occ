package org.agora.occ.dto.route.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.agora.occ.dto.jpl.response.JplResponse;
import org.agora.occ.enums.ActiveStatus;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class RouteSegmentDetailResponse {

    @JsonProperty("routeSegmentId")
    private UUID routeSegmentId;

    @JsonProperty("routeSegmentCode")
    private String routeSegmentCode;

    @JsonProperty("fromStationId")
    private UUID fromStationId;

    @JsonProperty("fromStationName")
    private String fromStationName;

    @JsonProperty("fromStationLatitude")
    private Double fromStationLatitude;

    @JsonProperty("fromStationLongitude")
    private Double fromStationLongitude;

    @JsonProperty("fromStationStatus")
    private ActiveStatus fromStationStatus;

    @JsonProperty("toStationId")
    private UUID toStationId;

    @JsonProperty("toStationName")
    private String toStationName;

    @JsonProperty("toStationLatitude")
    private Double toStationLatitude;

    @JsonProperty("toStationLongitude")
    private Double toStationLongitude;

    @JsonProperty("toStationStatus")
    private ActiveStatus toStationStatus;

    @JsonProperty("routeDuration")
    private Integer routeDuration;

    @JsonProperty("routeDistance")
    private Double routeDistance;

    @JsonProperty("routeStatus")
    private ActiveStatus routeStatus;

    @JsonProperty("segmentIndex")
    private Integer segmentIndex;

    @JsonProperty("jpls")
    private List<JplResponse> jpls;
}
