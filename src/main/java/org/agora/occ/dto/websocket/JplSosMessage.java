package org.agora.occ.dto.websocket;

import lombok.Data;
import org.agora.occ.entity.event.JplSosEvent;
import org.agora.occ.enums.ActiveStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * WebSocket push-message DTO for JPL SOS alerts.
 *
 * <p>
 * Built from a {@link JplSosEvent} via the {@link #from(JplSosEvent)} factory
 * method
 * and serialized to JSON before being sent to all subscribed clients.
 * </p>
 *
 * <p>
 * The {@code type} field is always {@code "jpl_sos"} to allow clients to
 * discriminate between different WebSocket message types.
 * </p>
 */
@Data
public class JplSosMessage {

    /** Discriminator constant to identify this message type on the client. */
    private final String type = "jpl_sos";

    private UUID sosId;
    private UUID jplId;
    private String jplName;
    private ActiveStatus jplStatus;
    private String jplNetwork;
    private String jplAddress;
    private Double jplLatitude;
    private Double jplLongitude;
    private Double heading;
    private Boolean isHealth;
    private Boolean isSirenOn;
    private Boolean isGateOpen;
    private Boolean isAnyObstacle;
    private Boolean isInstalled;
    private List<String> cameraStream;
    private UUID stationId;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant timestamp;

    /**
     * Creates a {@link JplSosMessage} from a {@link JplSosEvent} CDI event.
     *
     * @param event the CDI event carrying the SOS payload
     * @return a populated message DTO ready for JSON serialization
     */
    public static JplSosMessage from(JplSosEvent event) {
        JplSosMessage msg = new JplSosMessage();
        msg.setSosId(event.sosId());
        msg.setJplId(event.jplId());
        msg.setJplName(event.jplName());
        msg.setJplStatus(event.jplStatus());
        msg.setJplNetwork(event.jplNetwork());
        msg.setJplAddress(event.jplAddress());
        msg.setJplLatitude(event.jplLatitude());
        msg.setJplLongitude(event.jplLongitude());
        msg.setHeading(event.heading());
        msg.setIsHealth(event.isHealth());
        msg.setIsSirenOn(event.isSirenOn());
        msg.setIsGateOpen(event.isGateOpen());
        msg.setIsAnyObstacle(event.isAnyObstacle());
        msg.setIsInstalled(event.isInstalled());
        msg.setCameraStream(event.cameraStream());
        msg.setStationId(event.stationId());
        msg.setCreatedAt(event.createdAt());
        msg.setUpdatedAt(event.updatedAt());
        msg.setTimestamp(event.timestamp());
        return msg;
    }
}
