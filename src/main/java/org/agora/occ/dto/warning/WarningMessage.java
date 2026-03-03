package org.agora.occ.dto.warning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.agora.occ.entity.event.WarningReceivedEvent;
import org.agora.occ.enums.DangerLevel;
import org.agora.occ.enums.MessageConstant;
import org.agora.occ.enums.WarningLevel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * WebSocket message payload for warning alerts from JPL camera systems.
 * Built from a {@link WarningReceivedEvent} via the
 * {@link #from(WarningReceivedEvent)} factory.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarningMessage {

    private String type;
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

    /**
     * Creates a {@link WarningMessage} from a {@link WarningReceivedEvent}.
     *
     * @param event the CDI event containing all warning data
     * @return a fully populated warning message ready to be serialised and sent
     */
    public static WarningMessage from(WarningReceivedEvent event) {
        return WarningMessage.builder()
                .type(MessageConstant.TYPE_WARNING)
                .alertId(event.alertId())
                .jplId(event.jplId())
                .cameraId(event.cameraId())
                .jplCode(event.jplCode())
                .jplName(event.jplName())
                .trainId(event.trainId())
                .trainName(event.trainName())
                .trainCode(event.trainCode())
                .crowdLevel(event.crowdLevel())
                .warningLevel(event.warningLevel())
                .distanceKm(event.distanceKm())
                .speedKmh(event.speedKmh())
                .objectDetected(event.objectDetected())
                .alertTimestamp(event.alertTimestamp())
                .actionRequired(event.actionRequired())
                .colorIndicator(event.colorIndicator())
                .build();
    }
}
