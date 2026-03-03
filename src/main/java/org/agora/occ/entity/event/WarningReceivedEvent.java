package org.agora.occ.entity.event;

import org.agora.occ.enums.DangerLevel;
import org.agora.occ.enums.WarningLevel;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * CDI event fired when a safety warning is received from a JPL camera system.
 * Observed by {@link org.agora.occ.controller.websocket.WarningWebSocket}
 * to push alert notifications to subscribed clients.
 *
 * @param alertId        the unique identifier of the alert
 * @param jplId          the JPL station that detected the warning
 * @param cameraId       the ID of the camera that triggered the warning
 * @param jplCode        the human-readable JPL code
 * @param jplName        the human-readable JPL name
 * @param trainId        the train involved in the warning
 * @param trainName      the human-readable train name
 * @param trainCode      the human-readable train code
 * @param crowdLevel     the passenger crowd danger level at the platform
 * @param warningLevel   the severity level of the warning
 * @param distanceKm     the distance between train and JPL in kilometres
 * @param speedKmh       the train speed at the time of the warning
 * @param objectDetected the number of objects detected by the camera
 * @param alertTimestamp the time the alert was triggered
 * @param actionRequired description of the recommended action
 * @param colorIndicator hex or named colour for UI severity indicator
 */
public record WarningReceivedEvent(
        UUID alertId,
        UUID jplId,
        String cameraId,
        String jplCode,
        String jplName,
        UUID trainId,
        String trainName,
        String trainCode,
        DangerLevel crowdLevel,
        WarningLevel warningLevel,
        Double distanceKm,
        Double speedKmh,
        Integer objectDetected,
        Instant alertTimestamp,
        String actionRequired,
        String colorIndicator) {
}
