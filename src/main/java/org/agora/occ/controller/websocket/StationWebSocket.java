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
 * WebSocket handler for real-time station telemetry updates.
 *
 * <p>
 * Clients subscribe to a specific station and receive enriched
 * telemetry whenever a train passes within range of that station.
 *
 * <pre>
 * Subscribe: {"action":"subscribe","stationId":"&lt;uuid&gt;"}
 * </pre>
 */
@WebSocket(path = "/ws/stations")
@ApplicationScoped
public class StationWebSocket {

    private static final Logger LOG = Logger.getLogger(StationWebSocket.class);

    private final HealthRegistry healthRegistry;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    @jakarta.inject.Inject
    public StationWebSocket(HealthRegistry healthRegistry) {
        this.healthRegistry = healthRegistry;
    }

    /**
     * Map: Station ID -> Set of WebSocket connections subscribed to that station.
     */
    private final Map<UUID, Set<WebSocketConnection>> stationSubscriptions = new ConcurrentHashMap<>();

    /**
     * Logs a new incoming connection.
     *
     * @param connection the newly opened WebSocket connection
     */
    @OnOpen
    public void onOpen(WebSocketConnection connection) {
        LOG.infof("New Station WebSocket connection: %s", connection.id());
    }

    /**
     * Removes the connection from all station subscriptions on disconnect.
     *
     * @param connection the closed WebSocket connection
     */
    @OnClose
    public void onClose(WebSocketConnection connection) {
        stationSubscriptions.values().forEach(connections -> connections.remove(connection));
        LOG.infof("Station WebSocket connection closed: %s", connection.id());
    }

    /**
     * Handles subscribe/unsubscribe actions for station rooms.
     *
     * @param message    the raw JSON message text
     * @param connection the sender's WebSocket connection
     */
    @OnTextMessage
    public void onMessage(String message, WebSocketConnection connection) {
        try {
            WebSocketRequest request = objectMapper.readValue(message, WebSocketRequest.class);
            String action = request.getAction();

            if ("subscribe".equals(action) && request.getStationId() != null) {
                subscribeStation(request.getStationId(), connection);
            } else if ("unsubscribe".equals(action) && request.getStationId() != null) {
                unsubscribeStation(request.getStationId(), connection);
            }
        } catch (Exception e) {
            LOG.errorf("Failed to process message from %s: %s", connection.id(), e.getMessage());
        }
    }

    /**
     * Subscribes a connection to a station room.
     *
     * @param stationId  the station to subscribe to
     * @param connection the client connection
     */
    private void subscribeStation(UUID stationId, WebSocketConnection connection) {
        stationSubscriptions.computeIfAbsent(stationId, k -> ConcurrentHashMap.newKeySet()).add(connection);
        healthRegistry.recordConnection(HealthCategory.STATION, stationId, stationSubscriptions.get(stationId).size());
        LOG.infof("Connection %s subscribed to Station %s", connection.id(), stationId);
    }

    /**
     * Unsubscribes a connection from a station room.
     *
     * @param stationId  the station to unsubscribe from
     * @param connection the client connection
     */
    private void unsubscribeStation(UUID stationId, WebSocketConnection connection) {
        Set<WebSocketConnection> connections = stationSubscriptions.get(stationId);
        if (connections != null) {
            connections.remove(connection);
            healthRegistry.recordConnection(HealthCategory.STATION, stationId, connections.size());
            if (connections.isEmpty()) {
                stationSubscriptions.remove(stationId);
            }
        }
        LOG.infof("Connection %s unsubscribed from Station %s", connection.id(), stationId);
    }

    /**
     * Observes CDI {@link TelemetryBroadcastEvent} and pushes telemetry to clients
     * subscribed to any station that appears in {@code event.targetIds()}.
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

        if (event.targetIds() != null) {
            for (UUID targetId : event.targetIds()) {
                Set<WebSocketConnection> connections = stationSubscriptions.get(targetId);
                if (connections != null && !connections.isEmpty()) {
                    broadcast(connections, message);
                }
            }
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
