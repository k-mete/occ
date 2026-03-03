package org.agora.occ.controller.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.agora.occ.dto.warning.WarningMessage;
import org.agora.occ.entity.event.TelemetryBroadcastEvent;
import org.agora.occ.entity.event.WarningReceivedEvent;
import org.agora.occ.enums.HealthCategory;
import org.agora.occ.service.HealthRegistry;
import org.agora.occ.service.WarningService;
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket handler for real-time safety warning alerts.
 *
 * <p>
 * Clients can subscribe by train (with optional JPL filter) or by JPL directly:
 * <ul>
 * <li>{@code {"action":"subscribe","trainId":"<uuid>"}} — all warnings for a
 * train</li>
 * <li>{@code {"action":"subscribe","trainId":"<uuid>","jplId":"<uuid>"}} —
 * filtered by JPL</li>
 * <li>{@code {"action":"subscribe","jplId":"<uuid>"}} — receive telemetry for a
 * JPL</li>
 * </ul>
 *
 * <p>
 * Warnings are pushed via {@link WarningReceivedEvent};
 * telemetry position data is pushed via {@link TelemetryBroadcastEvent}.
 */
@WebSocket(path = "/ws/warnings")
@ApplicationScoped
public class WarningWebSocket {

    private static final Logger LOG = Logger.getLogger(WarningWebSocket.class);

    private final WarningService warningService;
    private final HealthRegistry healthRegistry;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);

    @jakarta.inject.Inject
    public WarningWebSocket(WarningService warningService, HealthRegistry healthRegistry) {
        this.warningService = warningService;
        this.healthRegistry = healthRegistry;
    }

    /**
     * Map: Train ID -> (Connection -> optional JPL ID filter).
     * A sentinel UUID {@link #SENTINEL_UUID} is stored when no JPL filter is set,
     * because {@link ConcurrentHashMap} does not allow null values.
     */
    private final Map<UUID, Map<WebSocketConnection, UUID>> trainSubscriptions = new ConcurrentHashMap<>();

    /** Map: JPL ID -> Set of connections subscribed to that JPL for telemetry. */
    private final Map<UUID, Set<WebSocketConnection>> jplSubscriptions = new ConcurrentHashMap<>();

    /** Sentinel value representing "no JPL filter" for train subscriptions. */
    private static final UUID SENTINEL_UUID = new UUID(0, 0);

    /**
     * Logs a new incoming connection.
     *
     * @param connection the newly opened WebSocket connection
     */
    @OnOpen
    public void onOpen(WebSocketConnection connection) {
        LOG.infof("New Warning WebSocket connection: %s", connection.id());
    }

    /**
     * Removes the connection from all subscription maps on disconnect.
     *
     * @param connection the closed WebSocket connection
     */
    @OnClose
    public void onClose(WebSocketConnection connection) {
        trainSubscriptions.values().forEach(innerMap -> innerMap.remove(connection));
        jplSubscriptions.values().forEach(connections -> connections.remove(connection));
        LOG.infof("Warning WebSocket connection closed: %s", connection.id());
    }

    /**
     * Handles subscribe/unsubscribe actions from clients.
     * Accepts {@code trainId} (with optional {@code jplId} filter) or {@code jplId}
     * alone.
     *
     * @param message    the raw JSON message text
     * @param connection the sender's WebSocket connection
     */
    @OnTextMessage
    public void onMessage(String message, WebSocketConnection connection) {
        try {
            ObjectNode json = objectMapper.readValue(message, ObjectNode.class);
            String action = json.get("action").asText();

            if ("subscribe".equals(action)) {
                handleSubscribe(json, connection);
            } else if ("unsubscribe".equals(action)) {
                handleUnsubscribe(json, connection);
            }
        } catch (Exception e) {
            LOG.errorf("Failed to process message from %s: %s", connection.id(), e.getMessage());
        }
    }

    /**
     * Routes a subscribe action to the correct subscription map.
     *
     * @param json       the parsed JSON object
     * @param connection the client connection
     */
    private void handleSubscribe(ObjectNode json, WebSocketConnection connection) {
        if (json.has("trainId")) {
            UUID trainId = UUID.fromString(json.get("trainId").asText());
            UUID jplIdFilter = json.has("jplId") ? UUID.fromString(json.get("jplId").asText()) : null;
            subscribeTrain(trainId, connection, jplIdFilter);
        } else if (json.has("jplId")) {
            UUID jplId = UUID.fromString(json.get("jplId").asText());
            subscribeJpl(jplId, connection);
        } else {
            LOG.warnf("Client %s tried to subscribe without trainId/jplId.", connection.id());
            try {
                connection.sendTextAndAwait("{\"error\":\"Invalid subscription. 'trainId' or 'jplId' is required.\"}");
            } catch (Exception e) {
                LOG.errorf("Failed to send error to client %s: %s", connection.id(), e.getMessage());
            }
        }
    }

    /**
     * Routes an unsubscribe action to the correct subscription map.
     *
     * @param json       the parsed JSON object
     * @param connection the client connection
     */
    private void handleUnsubscribe(ObjectNode json, WebSocketConnection connection) {
        if (json.has("trainId")) {
            UUID trainId = UUID.fromString(json.get("trainId").asText());
            unsubscribeTrain(trainId, connection);
        } else if (json.has("jplId")) {
            UUID jplId = UUID.fromString(json.get("jplId").asText());
            unsubscribeJpl(jplId, connection);
        }
    }

    /**
     * Subscribes a connection to a train room with an optional JPL filter.
     *
     * @param trainId     the train to subscribe to
     * @param connection  the client connection
     * @param jplIdFilter if non-null, only warnings from this JPL are sent
     */
    private void subscribeTrain(UUID trainId, WebSocketConnection connection, UUID jplIdFilter) {
        trainSubscriptions.computeIfAbsent(trainId, k -> new ConcurrentHashMap<>())
                .put(connection, jplIdFilter != null ? jplIdFilter : SENTINEL_UUID);
        healthRegistry.recordConnection(HealthCategory.TRAIN, trainId, trainSubscriptions.get(trainId).size());
        LOG.infof("Connection %s subscribed to Train %s with JPL filter %s",
                connection.id(), trainId, jplIdFilter);
    }

    /**
     * Unsubscribes a connection from a train room.
     *
     * @param trainId    the train to unsubscribe from
     * @param connection the client connection
     */
    private void unsubscribeTrain(UUID trainId, WebSocketConnection connection) {
        Map<WebSocketConnection, UUID> connections = trainSubscriptions.get(trainId);
        if (connections != null) {
            connections.remove(connection);
            healthRegistry.recordConnection(HealthCategory.TRAIN, trainId, connections.size());
            if (connections.isEmpty()) {
                trainSubscriptions.remove(trainId);
            }
            LOG.infof("Connection %s unsubscribed from Train %s", connection.id(), trainId);
        }
    }

    /**
     * Subscribes a connection to a JPL room for telemetry data.
     *
     * @param jplId      the JPL to subscribe to
     * @param connection the client connection
     */
    private void subscribeJpl(UUID jplId, WebSocketConnection connection) {
        jplSubscriptions.computeIfAbsent(jplId, k -> ConcurrentHashMap.newKeySet()).add(connection);
        healthRegistry.recordConnection(HealthCategory.JPL, jplId, jplSubscriptions.get(jplId).size());
        LOG.infof("Connection %s subscribed to JPL %s", connection.id(), jplId);
    }

    /**
     * Unsubscribes a connection from a JPL room.
     *
     * @param jplId      the JPL to unsubscribe from
     * @param connection the client connection
     */
    private void unsubscribeJpl(UUID jplId, WebSocketConnection connection) {
        Set<WebSocketConnection> connections = jplSubscriptions.get(jplId);
        if (connections != null) {
            connections.remove(connection);
            healthRegistry.recordConnection(HealthCategory.JPL, jplId, connections.size());
            if (connections.isEmpty()) {
                jplSubscriptions.remove(jplId);
            }
            LOG.infof("Connection %s unsubscribed from JPL %s", connection.id(), jplId);
        }
    }

    /**
     * Observes CDI {@link WarningReceivedEvent} and sends the warning to
     * all subscribed clients for the target train. Respects optional JPL filters:
     * a client with no filter receives all warnings; a client with a filter
     * only receives warnings from that specific JPL.
     *
     * @param event the warning event fired from the service layer
     */
    public void onWarningReceived(@Observes WarningReceivedEvent event) {
        WarningMessage warningMessage = WarningMessage.from(event);

        String message;
        try {
            message = objectMapper.writeValueAsString(warningMessage);
        } catch (Exception e) {
            LOG.errorf("Failed to serialize warning event: %s", e.getMessage());
            return;
        }

        UUID targetTrainId = event.trainId();
        UUID sourceJplId = event.jplId();
        int totalSent = 0;

        Map<WebSocketConnection, UUID> connections = trainSubscriptions.get(targetTrainId);
        if (connections != null && !connections.isEmpty()) {
            for (Map.Entry<WebSocketConnection, UUID> entry : connections.entrySet()) {
                WebSocketConnection conn = entry.getKey();
                UUID filterJplId = entry.getValue();

                // Send if: no filter (sentinel) OR filter matches the source JPL
                boolean shouldSend = filterJplId.equals(SENTINEL_UUID) || filterJplId.equals(sourceJplId);

                if (shouldSend) {
                    try {
                        conn.sendTextAndAwait(message);
                        totalSent++;
                    } catch (Exception e) {
                        LOG.errorf("Failed to send warning to client %s: %s", conn.id(), e.getMessage());
                    }
                }
            }
        }

        LOG.infof("Sent warning to %d clients subscribed to Train %s", totalSent, targetTrainId);

        // Persist the warning alert to the database
        try {
            warningService.save(event);
        } catch (Exception e) {
            LOG.errorf("Failed to persist warning event: %s", e.getMessage());
        }
    }

    /**
     * Observes CDI {@link TelemetryBroadcastEvent} and forwards position data
     * to clients subscribed to target JPLs in the event.
     *
     * @param event the telemetry broadcast event fired from the service layer
     */
    public void onTelemetryBroadcast(@Observes TelemetryBroadcastEvent event) {
        String message;
        try {
            message = objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            LOG.errorf("Failed to serialize telemetry event: %s", e.getMessage());
            return;
        }

        int totalSent = 0;
        for (UUID targetJplId : event.targetIds()) {
            Set<WebSocketConnection> connections = jplSubscriptions.get(targetJplId);
            if (connections != null && !connections.isEmpty()) {
                for (WebSocketConnection conn : connections) {
                    try {
                        conn.sendTextAndAwait(message);
                        totalSent++;
                    } catch (Exception e) {
                        LOG.errorf("Failed to send telemetry to client %s: %s", conn.id(), e.getMessage());
                    }
                }
            }
        }

        if (totalSent > 0) {
            LOG.debugf("Sent telemetry to %d clients across %d target JPLs",
                    totalSent, event.targetIds().size());
        }
    }
}
