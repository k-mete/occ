package org.agora.occ.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import lombok.RequiredArgsConstructor;
import org.agora.occ.dto.warning.request.WarningRequest;
import org.agora.occ.entity.event.WarningReceivedEvent;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
@RequiredArgsConstructor
public class BroadcastWarningService {

    private static final Logger LOG = Logger.getLogger(BroadcastWarningService.class);

    private final Event<WarningReceivedEvent> warningEvent;

    /**
     * Broadcasts a warning event received from a JPL.
     * Generates a unique alert ID if missing and fires the event for processing.
     *
     * @param command the warning request containing alert details
     */
    public void broadcastWarning(WarningRequest command) {
        LOG.debugv("Received warning command from Jpl: {0} for Train: {1}",
                command.getJplId(), command.getTrainId());

        UUID alertId = command.getAlertId() != null ? command.getAlertId() : UUID.randomUUID();
        Instant timestamp = command.getAlertTimestamp() != null ? command.getAlertTimestamp() : Instant.now();

        WarningReceivedEvent event = new WarningReceivedEvent(
                alertId,
                command.getJplId(),
                command.getCameraId(),
                command.getJplCode(),
                command.getJplName(),
                command.getTrainId(),
                command.getTrainName(),
                command.getTrainCode(),
                command.getCrowdLevel(),
                command.getWarningLevel(),
                command.getDistanceKm(),
                command.getSpeedKmh(),
                command.getObjectDetected(),
                timestamp,
                command.getActionRequired(),
                command.getColorIndicator());

        warningEvent.fire(event);
        LOG.infov("Warning event broadcasted. AlertID: {0}, Train: {1}, Level: {2}",
                alertId, command.getTrainId(), command.getWarningLevel());
    }
}
