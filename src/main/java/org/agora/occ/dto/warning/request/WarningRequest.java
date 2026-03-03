package org.agora.occ.dto.warning.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.agora.occ.enums.DangerLevel;
import org.agora.occ.enums.WarningLevel;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarningRequest {

        @JsonProperty("alertId")
        private UUID alertId;

        @NotNull(message = "JPL ID is required")
        @JsonProperty("jplId")
        private UUID jplId;

        @JsonProperty("cameraId")
        private String cameraId;

        @JsonProperty("jplCode")
        private String jplCode;

        @JsonProperty("jplName")
        private String jplName;

        @NotNull(message = "Train ID is required")
        @JsonProperty("trainId")
        private UUID trainId;

        @JsonProperty("trainName")
        private String trainName;

        @JsonProperty("trainCode")
        private String trainCode;

        @JsonProperty("crowdLevel")
        private DangerLevel crowdLevel;

        @JsonProperty("warningLevel")
        private WarningLevel warningLevel;

        @JsonProperty("distanceKm")
        private Double distanceKm;

        @JsonProperty("speedKmh")
        private Double speedKmh;

        @JsonProperty("objectDetected")
        private Integer objectDetected;

        @JsonProperty("totalObjectDetected")
        private Integer totalObjectDetected;

        @JsonProperty("alertTimestamp")
        private Instant alertTimestamp;

        @JsonProperty("actionRequired")
        private String actionRequired;

        @JsonProperty("colorIndicator")
        private String colorIndicator;
}
