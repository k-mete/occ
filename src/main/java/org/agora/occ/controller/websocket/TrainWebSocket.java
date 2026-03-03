package org.agora.occ.controller.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.agora.occ.dto.websocket.WebSocketRequest;
import org.agora.occ.entity.event.TelemetryBroadcastEvent;
import org.agora.occ.enums.HealthCategory;
import org.agora.occ.service.HealthRegistry;
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket handler for real-time train telemetry (position, speed, heading).
 *
 * <p>
 * Clients can subscribe using one of three scopes:
 * <ul>
 * <li>{@code jplId} — receive telemetry when a train is near that JPL</li>
 * <li>{@code occId} — receive all telemetry for all trains (no proximity
 * filter)</li>
 * <li>{@code trainId} — receive telemetry for a specific train only</li>
 * </ul>
 *
 * <p>
 * Telemetry is pushed via CDI {@link TelemetryBroadcastEvent} from the service
 * layer.
 */
@WebSocket(path = "/ws/trains")
@ApplicationScoped
public class TrainWebSocket {

    private static final Logger LOG = Logger.getLogger(TrainWebSocket.class);

    private final HealthRegistry healthRegistry;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @jakarta.inject.Inject
    public TrainWebSocket(HealthRegistry healthRegistry) {
        this.healthRegistry = healthRegistry;
    }

    /** Map: JPL ID -> subscribed connections (proximity-based). */
    private final Map<UUID, Set<WebSocketConnection>> jplSubscriptions = new ConcurrentHashMap<>();

    /** Map: OCC ID -> subscribed connections (all-train broadcast). */
    private final Map<UUID, Set<WebSocketConnection>> occSubscriptions = new ConcurrentHashMap<>();

    /** Map: Train ID -> subscribed connections (train-specific). */
    private final Map<UUID, Set<WebSocketConnection>> trainSubscriptions = new ConcurrentHashMap<>();

    /**
     * Logs a new incoming connection.
     *
     * @param connection the newly opened WebSocket connection
     */
    @OnOpen
    public void onOpen(WebSocketConnection connection) {
        LOG.infof("New Train WebSocket connection: %s", connection.id());
    }

    /**
     * Removes the connection from all subscription maps on disconnect.
     *
     * @param connection the closed WebSocket connection
     */
    @OnClose
    public void onClose(WebSocketConnection connection) {
        jplSubscriptions.values().forEach(connections -> connections.remove(connection));
        trainSubscriptions.values().forEach(connections -> connections.remove(connection));
        occSubscriptions.values().forEach(connections -> connections.remove(connection));
        LOG.infof("Train WebSocket connection closed: %s", connection.id());
    }

    /**
     * Handles subscribe/unsubscribe actions. Clients must provide exactly one of:
     * {@code jplId}, {@code trainId}, or {@code occId}.
     *
     * @param message    the raw JSON message text
     * @param connection the sender's WebSocket connection
     */
    @OnTextMessage
    public void onMessage(String message, WebSocketConnection connection) {
        try {
            WebSocketRequest request = objectMapper.readValue(message, WebSocketRequest.class);
            String action = request.getAction();

            if ("subscribe".equals(action)) {
                handleSubscribe(request, connection);
            } else if ("unsubscribe".equals(action)) {
                handleUnsubscribe(request, connection);
            }
        } catch (Exception e) {
            LOG.errorf("Failed to process message from %s: %s", connection.id(), e.getMessage());
        }
    }

    /**
     * Routes a subscribe action to the correct subscription map.
     *
     * @param request    the parsed WebSocket request
     * @param connection the client connection
     */
    private void handleSubscribe(WebSocketRequest request, WebSocketConnection connection) {
        if (request.getJplId() != null) {
            subscribeJpl(request.getJplId(), connection);
        } else if (request.getTrainId() != null) {
            subscribeTrain(request.getTrainId(), connection);
        } else if (request.getOccId() != null) {
            subscribeOcc(request.getOccId(), connection);
        }
    }

    /**
     * Routes an unsubscribe action to the correct subscription map.
     *
     * @param request    the parsed WebSocket request
     * @param connection the client connection
     */
    private void handleUnsubscribe(WebSocketRequest request, WebSocketConnection connection) {
        if (request.getJplId() != null) {
            unsubscribeJpl(request.getJplId(), connection);
        } else if (request.getTrainId() != null) {
            unsubscribeTrain(request.getTrainId(), connection);
        } else if (request.getOccId() != null) {
            unsubscribeOcc(request.getOccId(), connection);
        }
    }

    /**
     * Subscribes a connection to a JPL room.
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
        }
        LOG.infof("Connection %s unsubscribed from JPL %s", connection.id(), jplId);
    }

    /**
     * Subscribes a connection to an OCC room (receives all train telemetry).
     *
     * @param occId      the OCC to subscribe to
     * @param connection the client connection
     */
    private void subscribeOcc(UUID occId, WebSocketConnection connection) {
        occSubscriptions.computeIfAbsent(occId, k -> ConcurrentHashMap.newKeySet()).add(connection);
        LOG.infof("Connection %s subscribed to OCC %s", connection.id(), occId);
    }

    /**
     * Unsubscribes a connection from an OCC room.
     *
     * @param occId      the OCC to unsubscribe from
     * @param connection the client connection
     */
    private void unsubscribeOcc(UUID occId, WebSocketConnection connection) {
        Set<WebSocketConnection> connections = occSubscriptions.get(occId);
        if (connections != null) {
            connections.remove(connection);
            if (connections.isEmpty()) {
                occSubscriptions.remove(occId);
            }
        }
        LOG.infof("Connection %s unsubscribed from OCC %s", connection.id(), occId);
    }

    /**
     * Subscribes a connection to a train room.
     *
     * @param trainId    the train to subscribe to
     * @param connection the client connection
     */
    private void subscribeTrain(UUID trainId, WebSocketConnection connection) {
        trainSubscriptions.computeIfAbsent(trainId, k -> ConcurrentHashMap.newKeySet()).add(connection);
        healthRegistry.recordConnection(HealthCategory.TRAIN, trainId, trainSubscriptions.get(trainId).size());
        LOG.infof("Connection %s subscribed to Train %s", connection.id(), trainId);
    }

    /**
     * Unsubscribes a connection from a train room.
     *
     * @param trainId    the train to unsubscribe from
     * @param connection the client connection
     */
    private void unsubscribeTrain(UUID trainId, WebSocketConnection connection) {
        Set<WebSocketConnection> connections = trainSubscriptions.get(trainId);
        if (connections != null) {
            connections.remove(connection);
            healthRegistry.recordConnection(HealthCategory.TRAIN, trainId, connections.size());
            if (connections.isEmpty()) {
                trainSubscriptions.remove(trainId);
            }
        }
        LOG.infof("Connection %s unsubscribed from Train %s", connection.id(), trainId);
    }

    /**
     * Observes CDI {@link TelemetryBroadcastEvent} and broadcasts enriched
     * telemetry to:
     * <ol>
     * <li>Clients subscribed to JPLs within {@code event.targetIds()}</li>
     * <li>All clients subscribed to any OCC (no proximity filter)</li>
     * <li>Clients subscribed directly to the train ({@code event.trainId()})</li>
     * </ol>
     *
     * @param event the telemetry broadcast event fired from the service layer
     */
    public void onTelemetryBroadcast(@Observes TelemetryBroadcastEvent event) {
        LOG.infof("TrainWebSocket received TelemetryBroadcastEvent for train %s", event.trainId());

        String message;
        try {
            message = objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            LOG.errorf("Failed to serialize telemetry event: %s", e.getMessage());
            return;
        }

        // 1. Broadcast to clients subscribed to target JPLs (proximity-based)
        if (event.targetIds() != null) {
            for (UUID targetId : event.targetIds()) {
                Set<WebSocketConnection> jplConns = jplSubscriptions.get(targetId);
                if (jplConns != null) {
                    broadcast(jplConns, message);
                }
            }
        }

        // 2. Broadcast to ALL clients subscribed to any OCC (no proximity filtering)
        occSubscriptions.forEach((occId, conns) -> {
            LOG.infof("Broadcasting to %d connections for OCC %s", conns.size(), occId);
            broadcast(conns, message);
        });

        // 3. Broadcast to clients subscribed directly to this train
        Set<WebSocketConnection> trainConns = trainSubscriptions.get(event.trainId());
        if (trainConns != null) {
            broadcast(trainConns, message);
        }
    }

    /**
     * Sends a message to a set of WebSocket connections.
     *
     * @param connections the target connections
     * @param message     the JSON string to send
     */
    private void broadcast(Set<WebSocketConnection> connections, String message) {
        for (WebSocketConnection conn : connections) {
            try {
                conn.sendTextAndAwait(message);
            } catch (Exception e) {
                LOG.errorf("Failed to send telemetry to client %s: %s", conn.id(), e.getMessage());
            }
        }
    }
}
