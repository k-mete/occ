package org.agora.occ.dto.telemetry.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@Jacksonized
@Schema(description = "Request payload for ingesting train telemetry data")
public class TelemetryRequest {
    @NotNull(message = "Train ID is required")
    @JsonProperty("trainId")
    @Schema(description = "Unique identifier of the train", example = "3c4d5e6f-7a8b-9c0d-1e2f-3a4b5c6d7e8f")
    private UUID trainId;

    @NotNull(message = "Train Code is required")
    @JsonProperty("trainCode")
    @Schema(description = "Unique code identifying the train", example = "KA-123")
    private String trainCode;

    @Min(value = -90, message = "Latitude must be >= -90")
    @Max(value = 90, message = "Latitude must be <= 90")
    @JsonProperty("trainLatitude")
    @Schema(description = "Current latitude coordinate of the train", example = "-6.176563")
    private double trainLatitude;

    @Min(value = -180, message = "Longitude must be >= -180")
    @Max(value = 180, message = "Longitude must be <= 180")
    @JsonProperty("trainLongitude")
    @Schema(description = "Current longitude coordinate of the train", example = "106.830845")
    private double trainLongitude;

    @JsonProperty("speed")
    @Schema(description = "Current speed of the train in km/h", example = "80.5")
    private double speed;

    @Min(value = 0, message = "Heading must be >= 0")
    @Max(value = 360, message = "Heading must be <= 360")
    @JsonProperty("heading")
    @Schema(description = "Current heading/direction of the train in degrees (0-360)")
    private double heading;

    @JsonProperty("timeStamp")
    @Schema(description = "Timestamp of when the telemetry was captured", example = "2026-01-21T10:30:00.000Z")
    private Instant timestamp;
}
