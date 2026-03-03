package org.agora.occ.entity.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * CDI event fired when train telemetry is ingested and enriched.
 * Observed by {@link org.agora.occ.controller.websocket.TrainWebSocket}
 * and {@link org.agora.occ.controller.websocket.StationWebSocket}
 * to push position data to subscribed clients.
 *
 * @param trainId   the unique identifier of the train
 * @param trainCode the human-readable train code
 * @param latitude  the train's current latitude
 * @param longitude the train's current longitude
 * @param speed     the train's current speed in km/h
 * @param heading   the train's heading in degrees
 * @param timestamp the time the telemetry was recorded
 * @param targetIds the list of JPL/station IDs within range of this train
 */
public record TelemetryBroadcastEvent(
        UUID trainId,
        String trainCode,
        double latitude,
        double longitude,
        double speed,
        double heading,
        Instant timestamp,
        List<UUID> targetIds) {
}
