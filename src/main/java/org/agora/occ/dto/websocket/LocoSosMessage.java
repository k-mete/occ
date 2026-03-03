package org.agora.occ.dto.websocket;

import lombok.Data;
import org.agora.occ.entity.event.LocoSosEvent;
import org.agora.occ.enums.ActiveStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * WebSocket push-message DTO for Loco SOS alerts.
 *
 * <p>
 * Built from a {@link LocoSosEvent} via the {@link #from(LocoSosEvent)} factory
 * method
 * and serialized to JSON before being sent to all subscribed clients.
 * </p>
 *
 * <p>
 * The {@code type} field is always {@code "loco_sos"} to allow clients to
 * discriminate between different WebSocket message types.
 * </p>
 */
@Data
public class LocoSosMessage {

    /** Discriminator constant to identify this message type on the client. */
    private final String type = "loco_sos";

    private UUID sosId;
    private UUID trainId;
    private String trainName;
    private String trainCode;
    private String trainNetworkIp;
    private ActiveStatus trainStatus;
    private Boolean isTrainOnline;
    private Double trainLastKnownLatitude;
    private Double trainLastKnownLongitude;
    private UUID routeId;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant timestamp;

    /**
     * Creates a {@link LocoSosMessage} from a {@link LocoSosEvent} CDI event.
     *
     * @param event the CDI event carrying the SOS payload
     * @return a populated message DTO ready for JSON serialization
     */
    public static LocoSosMessage from(LocoSosEvent event) {
        LocoSosMessage msg = new LocoSosMessage();
        msg.setSosId(event.sosId());
        msg.setTrainId(event.trainId());
        msg.setTrainName(event.trainName());
        msg.setTrainCode(event.trainCode());
        msg.setTrainNetworkIp(event.trainNetworkIp());
        msg.setTrainStatus(event.trainStatus());
        msg.setIsTrainOnline(event.isTrainOnline());
        msg.setTrainLastKnownLatitude(event.trainLastKnownLatitude());
        msg.setTrainLastKnownLongitude(event.trainLastKnownLongitude());
        msg.setRouteId(event.routeId());
        msg.setCreatedAt(event.createdAt());
        msg.setUpdatedAt(event.updatedAt());
        msg.setTimestamp(event.timestamp());
        return msg;
    }
}
