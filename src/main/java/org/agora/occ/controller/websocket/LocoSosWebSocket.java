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
import org.agora.occ.dto.websocket.LocoSosMessage;
import org.agora.occ.entity.event.LocoSosEvent;
import org.agora.occ.enums.HealthCategory;
import org.agora.occ.service.HealthRegistry;
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket handler for real-time Loco SOS alerts.
 *
 * <p>
 * Clients (JPL, station, and OCC) subscribe to SOS alerts from a specific
 * locomotive by providing the target {@code trainId} in their subscription
 * message.
 * </p>
 *
 * <p>
 * Subscribe / unsubscribe message format:
 * </p>
 * 
 * <pre>
 *   { "action": "subscribe",   "trainId": "&lt;uuid&gt;" }
 *   { "action": "unsubscribe", "trainId": "&lt;uuid&gt;" }
 * </pre>
 *
 * <p>
 * When a {@link LocoSosEvent} CDI event is observed, a {@link LocoSosMessage}
 * payload is serialized and pushed to all connections subscribed to the event's
 * {@code trainId}.
 * </p>
 */
@WebSocket(path = "/ws/loco-sos")
@ApplicationScoped
public class LocoSosWebSocket {

    private static final Logger LOG = Logger.getLogger(LocoSosWebSocket.class);

    private final HealthRegistry healthRegistry;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);

    @jakarta.inject.Inject
    public LocoSosWebSocket(HealthRegistry healthRegistry) {
        this.healthRegistry = healthRegistry;
    }

    /**
     * Map: Train ID -&gt; Set of connections subscribed to that train's SOS alerts.
     */
    private final Map<UUID, Set<WebSocketConnection>> trainSubscriptions = new ConcurrentHashMap<>();

    /**
     * Logs a new incoming WebSocket connection.
     *
     * @param connection the newly opened WebSocket connection
     */
    @OnOpen
    public void onOpen(WebSocketConnection connection) {
        LOG.infof("New LocoSos WebSocket connection: %s", connection.id());
    }

    /**
     * Removes the connection from all subscription maps on disconnect.
     *
     * @param connection the closed WebSocket connection
     */
    @OnClose
    public void onClose(WebSocketConnection connection) {
        trainSubscriptions.values().forEach(connections -> connections.remove(connection));
        LOG.infof("LocoSos WebSocket connection closed: %s", connection.id());
    }

    /**
     * Handles incoming text messages from clients. Supports {@code subscribe}
     * and {@code unsubscribe} actions identified by the {@code trainId} key.
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
            LOG.errorf("Failed to process LocoSos message from %s: %s", connection.id(), e.getMessage());
        }
    }

    /**
     * Processes a subscribe action by registering the connection under
     * the given {@code trainId}.
     *
     * @param json       the parsed JSON object from the client
     * @param connection the client connection to register
     */
    private void handleSubscribe(ObjectNode json, WebSocketConnection connection) {
        if (!json.has("trainId")) {
            sendError(connection, "Invalid subscription. 'trainId' is required.");
            return;
        }
        UUID trainId = UUID.fromString(json.get("trainId").asText());
        trainSubscriptions.computeIfAbsent(trainId, k -> ConcurrentHashMap.newKeySet()).add(connection);
        healthRegistry.recordConnection(HealthCategory.TRAIN, trainId, trainSubscriptions.get(trainId).size());
        LOG.infof("Connection %s subscribed to Loco SOS for trainId=%s", connection.id(), trainId);
    }

    /**
     * Processes an unsubscribe action by removing the connection from the
     * given {@code trainId} subscription set.
     *
     * @param json       the parsed JSON object from the client
     * @param connection the client connection to deregister
     */
    private void handleUnsubscribe(ObjectNode json, WebSocketConnection connection) {
        if (!json.has("trainId")) {
            return;
        }
        UUID trainId = UUID.fromString(json.get("trainId").asText());
        Set<WebSocketConnection> connections = trainSubscriptions.get(trainId);
        if (connections != null) {
            connections.remove(connection);
            healthRegistry.recordConnection(HealthCategory.TRAIN, trainId, connections.size());
            if (connections.isEmpty()) {
                trainSubscriptions.remove(trainId);
            }
            LOG.infof("Connection %s unsubscribed from Loco SOS for trainId=%s", connection.id(), trainId);
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
     * Observes a {@link LocoSosEvent} CDI event and pushes the serialized
     * {@link LocoSosMessage} to all connections subscribed to the event's
     * {@code trainId}.
     *
     * @param event the Loco SOS event fired from the service layer
     */
    public void onLocoSosReceived(@Observes LocoSosEvent event) {
        LocoSosMessage message = LocoSosMessage.from(event);

        String payload;
        try {
            payload = objectMapper.writeValueAsString(message);
        } catch (Exception e) {
            LOG.errorf("Failed to serialize LocoSosEvent: %s", e.getMessage());
            return;
        }

        Set<WebSocketConnection> connections = trainSubscriptions.get(event.trainId());
        if (connections == null || connections.isEmpty()) {
            return;
        }

        int totalSent = 0;
        for (WebSocketConnection conn : connections) {
            try {
                conn.sendTextAndAwait(payload);
                totalSent++;
            } catch (Exception e) {
                LOG.errorf("Failed to send LocoSos alert to client %s: %s", conn.id(), e.getMessage());
            }
        }

        LOG.infof("Sent Loco SOS alert (sosId=%s) to %d clients subscribed to trainId=%s",
                event.sosId(), totalSent, event.trainId());
    }
}
