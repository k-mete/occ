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
import org.agora.occ.dto.websocket.JplSosMessage;
import org.agora.occ.entity.event.JplSosEvent;
import org.agora.occ.enums.HealthCategory;
import org.agora.occ.service.HealthRegistry;
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket handler for real-time JPL SOS alerts.
 *
 * <p>
 * Clients (loco, station, and OCC) subscribe to SOS alerts from a specific JPL
 * by providing the target {@code jplId} in their subscription message.
 * </p>
 *
 * <p>
 * Subscribe / unsubscribe message format:
 * </p>
 * 
 * <pre>
 *   { "action": "subscribe",   "jplId": "&lt;uuid&gt;" }
 *   { "action": "unsubscribe", "jplId": "&lt;uuid&gt;" }
 * </pre>
 *
 * <p>
 * When a {@link JplSosEvent} CDI event is observed, a {@link JplSosMessage}
 * payload is serialized and pushed to all connections subscribed to the event's
 * {@code jplId}.
 * </p>
 */
@WebSocket(path = "/ws/jpl-sos")
@ApplicationScoped
public class JplSosWebSocket {

    private static final Logger LOG = Logger.getLogger(JplSosWebSocket.class);

    private final HealthRegistry healthRegistry;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);

    @jakarta.inject.Inject
    public JplSosWebSocket(HealthRegistry healthRegistry) {
        this.healthRegistry = healthRegistry;
    }

    /** Map: JPL ID -&gt; Set of connections subscribed to that JPL's SOS alerts. */
    private final Map<UUID, Set<WebSocketConnection>> jplSubscriptions = new ConcurrentHashMap<>();

    /**
     * Logs a new incoming WebSocket connection.
     *
     * @param connection the newly opened WebSocket connection
     */
    @OnOpen
    public void onOpen(WebSocketConnection connection) {
        LOG.infof("New JplSos WebSocket connection: %s", connection.id());
    }

    /**
     * Removes the connection from all subscription maps on disconnect.
     *
     * @param connection the closed WebSocket connection
     */
    @OnClose
    public void onClose(WebSocketConnection connection) {
        jplSubscriptions.values().forEach(connections -> connections.remove(connection));
        LOG.infof("JplSos WebSocket connection closed: %s", connection.id());
    }

    /**
     * Handles incoming text messages from clients. Supports {@code subscribe}
     * and {@code unsubscribe} actions identified by the {@code jplId} key.
     *
     * @param message    the raw JSON message text from the client
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
            LOG.errorf("Failed to process JplSos message from %s: %s", connection.id(), e.getMessage());
        }
    }

    /**
     * Processes a subscribe action by registering the connection under
     * the given {@code jplId}.
     *
     * @param json       the parsed JSON object from the client
     * @param connection the client connection to register
     */
    private void handleSubscribe(ObjectNode json, WebSocketConnection connection) {
        if (!json.has("jplId")) {
            sendError(connection, "Invalid subscription. 'jplId' is required.");
            return;
        }
        UUID jplId = UUID.fromString(json.get("jplId").asText());
        jplSubscriptions.computeIfAbsent(jplId, k -> ConcurrentHashMap.newKeySet()).add(connection);
        healthRegistry.recordConnection(HealthCategory.JPL, jplId, jplSubscriptions.get(jplId).size());
        LOG.infof("Connection %s subscribed to JPL SOS for jplId=%s", connection.id(), jplId);
    }

    /**
     * Processes an unsubscribe action by removing the connection from the
     * given {@code jplId} subscription set.
     *
     * @param json       the parsed JSON object from the client
     * @param connection the client connection to deregister
     */
    private void handleUnsubscribe(ObjectNode json, WebSocketConnection connection) {
        if (!json.has("jplId")) {
            return;
        }
        UUID jplId = UUID.fromString(json.get("jplId").asText());
        Set<WebSocketConnection> connections = jplSubscriptions.get(jplId);
        if (connections != null) {
            connections.remove(connection);
            healthRegistry.recordConnection(HealthCategory.JPL, jplId, connections.size());
            if (connections.isEmpty()) {
                jplSubscriptions.remove(jplId);
            }
            LOG.infof("Connection %s unsubscribed from JPL SOS for jplId=%s", connection.id(), jplId);
        }
    }

    /**
     * Sends a JSON error message to a single client connection.
     *
     * @param connection the target connection
     * @param errorMsg   the error description
     */
    private void sendError(WebSocketConnection connection, String errorMsg) {
        try {
            connection.sendTextAndAwait("{\"error\":\"" + errorMsg + "\"}");
        } catch (Exception e) {
            LOG.errorf("Failed to send error to client %s: %s", connection.id(), e.getMessage());
        }
    }

    /**
     * Observes a {@link JplSosEvent} CDI event and pushes the serialized
     * {@link JplSosMessage} to all connections subscribed to the event's
     * {@code jplId}.
     *
     * @param event the JPL SOS event fired from the service layer
     */
    public void onJplSosReceived(@Observes JplSosEvent event) {
        JplSosMessage message = JplSosMessage.from(event);

        String payload;
        try {
            payload = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            LOG.errorf("Failed to serialize JplSosEvent: %s", e.getMessage());
            return;
        }

        Set<WebSocketConnection> connections = jplSubscriptions.get(event.jplId());
        if (connections == null || connections.isEmpty()) {
            return;
        }

        int totalSent = 0;
        for (WebSocketConnection conn : connections) {
            try {
                conn.sendTextAndAwait(payload);
                totalSent++;
            } catch (Exception e) {
                LOG.errorf("Failed to send JplSos alert to client %s: %s", conn.id(), e.getMessage());
            }
        }

        LOG.infof("Sent JPL SOS alert (sosId=%s) to %d clients subscribed to jplId=%s",
                event.sosId(), totalSent, event.jplId());
    }
}
