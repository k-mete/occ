package org.agora.occ.dto.warning;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.agora.occ.entity.WarningEntity;
import org.agora.occ.enums.DangerLevel;
import org.agora.occ.enums.WarningLevel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * REST response DTO for warning history queries.
 * Mapped directly from {@link WarningEntity}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class WarningResponse {

    private UUID alertId;
    private UUID jplId;
    private String cameraId;
    private String jplCode;
    private String jplName;
    private UUID trainId;
    private String trainName;
    private String trainCode;
    private DangerLevel crowdLevel;
    private WarningLevel warningLevel;
    private Double distanceKm;
    private Double speedKmh;
    private Integer objectDetected;
    private Instant alertTimestamp;
    private String actionRequired;
    private String colorIndicator;
    private Boolean isHealth;
    private Boolean isSirenOn;
    private Boolean isGateOpen;
    private Boolean isAnyObstacle;
    private Boolean isInstalled;
    private List<String> cameraStream;

    /**
     * Converts a JPA entity into this response representation.
     *
     * @param entity the persisted warning record
     * @return the client-facing response DTO
     */
    public static WarningResponse from(WarningEntity entity) {
        return WarningResponse.builder()
                .alertId(entity.getAlertId())
                .jplId(entity.getJpl() != null ? entity.getJpl().getId() : null)
                .cameraId(entity.getCameraId())
                .jplCode(entity.getJplCode())
                .jplName(entity.getJplName())
                .trainId(entity.getTrainId())
                .trainName(entity.getTrainName())
                .trainCode(entity.getTrainCode())
                .crowdLevel(entity.getCrowdLevel())
                .warningLevel(entity.getWarningLevel())
                .distanceKm(entity.getDistanceKm())
                .speedKmh(entity.getSpeedKmh())
                .objectDetected(entity.getObjectDetected())
                .alertTimestamp(entity.getAlertTimestamp())
                .actionRequired(entity.getActionRequired())
                .colorIndicator(entity.getColorIndicator())
                .isHealth(entity.getIsHealth())
                .isSirenOn(entity.getIsSirenOn())
                .isGateOpen(entity.getIsGateOpen())
                .isAnyObstacle(entity.getIsAnyObstacle())
                .isInstalled(entity.getIsInstalled())
                .cameraStream(entity.getCameraStream())
                .build();
    }
}
