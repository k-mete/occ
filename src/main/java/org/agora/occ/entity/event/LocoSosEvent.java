package org.agora.occ.entity.event;

import org.agora.occ.enums.ActiveStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * CDI event fired when a locomotive triggers an SOS alert.
 * Observed by {@link org.agora.occ.controller.websocket.LocoSosWebSocket}
 * to push real-time SOS notifications to all subscribed clients
 * (JPL, station, and OCC).
 *
 * @param sosId                   unique identifier of the SOS alert
 * @param trainId                 the locomotive that triggered the SOS
 * @param trainName               human-readable name of the train
 * @param trainCode               short operational code of the train
 * @param trainNetworkIp          IP address of the on-board network module
 * @param trainStatus             current operational status of the locomotive
 * @param isTrainOnline           whether the locomotive is currently online
 * @param trainLastKnownLatitude  last known GPS latitude of the locomotive
 * @param trainLastKnownLongitude last known GPS longitude of the locomotive
 * @param routeId                 identifier of the route the train is operating
 *                                on
 * @param createdAt               timestamp when the train record was first
 *                                created
 * @param updatedAt               timestamp of the last update to the train
 *                                record
 * @param timestamp               timestamp when this SOS alert was triggered
 */
public record LocoSosEvent(
        UUID sosId,
        UUID trainId,
        String trainName,
        String trainCode,
        String trainNetworkIp,
        ActiveStatus trainStatus,
        Boolean isTrainOnline,
        Double trainLastKnownLatitude,
        Double trainLastKnownLongitude,
        UUID routeId,
        Instant createdAt,
        Instant updatedAt,
        Instant timestamp) {
}
