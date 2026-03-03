package org.agora.occ.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import lombok.RequiredArgsConstructor;
import org.agora.occ.dto.telemetry.request.TelemetryRequest;
import org.agora.occ.entity.event.TelemetryBroadcastEvent;
import org.jboss.logging.Logger;

import java.util.Collections;

@ApplicationScoped
@RequiredArgsConstructor
public class IngestTelemetryService {

    private static final Logger LOG = Logger.getLogger(IngestTelemetryService.class);

    private final Event<TelemetryBroadcastEvent> broadcastEvent;

    /**
     * Ingests telemetry data from a train.
     * Fires a broadcast event immediately to notify subscribed clients.
     *
     * @param command the telemetry data to ingest
     */
    public void execute(TelemetryRequest command) {
        LOG.debugv("Ingesting telemetry for Train: {0}, Code: {1}", command.getTrainId(), command.getTrainCode());

        // Publish domain event directly to WebSocket listeners
        // For v1, targetIds is empty (no route proximity filter yet)
        TelemetryBroadcastEvent event = new TelemetryBroadcastEvent(
                command.getTrainId(),
                command.getTrainCode(),
                command.getTrainLatitude(),
                command.getTrainLongitude(),
                command.getSpeed(),
                command.getHeading(),
                command.getTimestamp(),
                Collections.emptyList());

        broadcastEvent.fire(event);
        LOG.debug("Telemetry broadcast event fired");
    }
}
