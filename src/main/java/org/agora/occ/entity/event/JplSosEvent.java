package org.agora.occ.entity.event;

import org.agora.occ.enums.ActiveStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * CDI event fired when a JPL station triggers an SOS alert.
 * Observed by {@link org.agora.occ.controller.websocket.JplSosWebSocket}
 * to push real-time SOS notifications to all subscribed clients
 * (loco, station, and OCC).
 *
 * @param sosId         unique identifier of the SOS alert
 * @param jplId         the JPL station that triggered the SOS
 * @param jplName       human-readable name of the JPL station
 * @param jplStatus     current operational status of the JPL
 * @param jplNetwork    network address/SSID of the JPL device
 * @param jplAddress    physical street address of the JPL
 * @param jplLatitude   GPS latitude of the JPL location
 * @param jplLongitude  GPS longitude of the JPL location
 * @param heading       directional heading in degrees (default 0)
 * @param isHealth      whether the JPL hardware reports a healthy state
 * @param isSirenOn     whether the siren is currently active at the JPL
 * @param isGateOpen    whether the level crossing gate is open
 * @param isAnyObstacle whether any obstacle is detected on the tracks
 * @param isInstalled   whether the JPL hardware is installed and active
 * @param cameraStream  list of RTSP camera stream URLs for this JPL
 * @param stationId     identifier of the parent station this JPL belongs to
 * @param createdAt     timestamp when the JPL record was first created
 * @param updatedAt     timestamp of the last update to the JPL record
 * @param timestamp     timestamp when this SOS alert was triggered
 */
public record JplSosEvent(
        UUID sosId,
        UUID jplId,
        String jplName,
        ActiveStatus jplStatus,
        String jplNetwork,
        String jplAddress,
        Double jplLatitude,
        Double jplLongitude,
        Double heading,
        Boolean isHealth,
        Boolean isSirenOn,
        Boolean isGateOpen,
        Boolean isAnyObstacle,
        Boolean isInstalled,
        List<String> cameraStream,
        UUID stationId,
        Instant createdAt,
        Instant updatedAt,
        Instant timestamp) {
}
