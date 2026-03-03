package org.agora.occ.dto.telemetry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Enriched telemetry broadcast response sent to WebSocket clients.
 * Carries train position, speed, heading, and associated trip/route/station
 * data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryBroadcastResponse {

    @JsonProperty("type")
    private String type;

    @JsonProperty("trainId")
    private UUID trainId;

    @JsonProperty("trainCode")
    private String trainCode;

    @JsonProperty("trainLatitude")
    private Double trainLatitude;

    @JsonProperty("trainLongitude")
    private Double trainLongitude;

    @JsonProperty("speed")
    private Double speed;

    @JsonProperty("heading")
    private Double heading;

    // Trip information
    @JsonProperty("tripId")
    private UUID tripId;

    @JsonProperty("isFlow")
    private Boolean isFlow;

    @JsonProperty("isTripStart")
    private Boolean isTripStart;

    @JsonProperty("tripStart")
    private Instant tripStart;

    @JsonProperty("tripEnd")
    private Instant tripEnd;

    @JsonProperty("tripLastUpdate")
    private Instant tripLastUpdate;

    // Route information
    @JsonProperty("routeId")
    private UUID routeId;

    @JsonProperty("routeCode")
    private String routeCode;

    @JsonProperty("fromStation")
    private UUID fromStation;

    @JsonProperty("toStation")
    private UUID toStation;

    @JsonProperty("fromStationName")
    private String fromStationName;

    @JsonProperty("toStationName")
    private String toStationName;

    @JsonProperty("timeStamp")
    private Instant timeStamp;
}
