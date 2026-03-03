package org.agora.occ.controller.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.dto.websocket.WebSocketRequest;
import org.jboss.logging.Logger;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket handler for live camera stream events.
 *
 * <p>
 * Clients subscribe to a JPL's stream by sending:
 * 
 * <pre>
 * {"action":"subscribe","jplId":"&lt;uuid&gt;"}
 * </pre>
 *
 * <p>
 * JPL devices push {@code stream_status}, {@code ack}, or {@code error}
 * messages.
 * These are relayed to all subscribers of that JPL, excluding the original
 * sender.
 */
@WebSocket(path = "/ws/live-stream")
@ApplicationScoped
public class LiveStreamWebSocket {

    private static final Logger LOG = Logger.getLogger(LiveStreamWebSocket.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Map: JPL ID -> Set of WebSocket connections subscribed to that JPL's stream.
     */
    private final Map<UUID, Set<WebSocketConnection>> subscriptions = new ConcurrentHashMap<>();

    /**
     * Logs a new incoming connection.
     *
     * @param connection the newly opened WebSocket connection
     */
    @OnOpen
    public void onOpen(WebSocketConnection connection) {
        LOG.infof("New Live Stream WebSocket connection: %s", connection.id());
    }

    /**
     * Removes the connection from all JPL subscriptions on disconnect.
     *
     * @param connection the closed WebSocket connection
     */
    @OnClose
    public void onClose(WebSocketConnection connection) {
        subscriptions.values().forEach(subs -> subs.remove(connection));
        LOG.infof("Live Stream WebSocket connection closed: %s", connection.id());
    }

    /**
     * Processes an incoming text message.
     * Handles subscribe/unsubscribe actions and relays stream data messages.
     *
     * @param message    the raw JSON message text
     * @param connection the sender's WebSocket connection
     */
    @OnTextMessage
    public void onMessage(String message, WebSocketConnection connection) {
        try {
            WebSocketRequest request = objectMapper.readValue(message, WebSocketRequest.class);
            UUID targetId = extractTargetId(request);
            if (targetId != null) {
                handleTargetMessage(targetId, request, message, connection);
            } else {
                handleMissingTarget(request, connection);
            }
        } catch (Exception e) {
            LOG.errorf("Error parsing message from %s: %s", connection.id(), e.getMessage());
        }
    }

    /**
     * Routes an incoming message to the appropriate handler based on action/type.
     *
     * @param targetId   the JPL ID extracted from the request
     * @param request    the parsed request object
     * @param message    the original raw JSON string (for relay)
     * @param connection the sender's WebSocket connection
     */
    private void handleTargetMessage(UUID targetId, WebSocketRequest request, String message,
            WebSocketConnection connection) {
        String action = request.getAction();
        String type = request.getType();

        if ("subscribe".equals(action)) {
            subscribe(targetId, connection);
        } else if ("unsubscribe".equals(action)) {
            unsubscribe(targetId, connection);
        } else if ("stream_status".equals(type) || "stream_status".equals(action)) {
            LOG.infof("Broadcasting stream status for JPL %s to subscribers (excluding sender)", targetId);
            broadcast(targetId, message, connection);
        } else if ("ack".equals(type) || "error".equals(type)) {
            LOG.infof("Broadcasting %s for JPL %s to subscribers (excluding sender)", type, targetId);
            broadcast(targetId, message, connection);
        }
    }

    /**
     * Logs a warning when a subscribe/unsubscribe action is missing a JPL ID.
     *
     * @param request    the parsed request
     * @param connection the sender's connection
     */
    private void handleMissingTarget(WebSocketRequest request, WebSocketConnection connection) {
        String action = request.getAction();
        if ("subscribe".equals(action) || "unsubscribe".equals(action)) {
            LOG.warnf("Action %s received without valid target ID (jplId needed) from %s",
                    action, connection.id());
        }
    }

    /**
     * Extracts the target JPL ID from the incoming request.
     *
     * @param request the parsed WebSocket request
     * @return the JPL UUID, or {@code null} if not present
     */
    private UUID extractTargetId(WebSocketRequest request) {
        return request.getJplId();
    }

    /**
     * Subscribes a connection to a JPL's stream room.
     *
     * @param jplId      the JPL to subscribe to
     * @param connection the client connection
     */
    private void subscribe(UUID jplId, WebSocketConnection connection) {
        subscriptions.computeIfAbsent(jplId, k -> ConcurrentHashMap.newKeySet()).add(connection);
        LOG.infof("Connection %s subscribed to stream room for JPL %s", connection.id(), jplId);
    }

    /**
     * Unsubscribes a connection from a JPL's stream room.
     *
     * @param jplId      the JPL to unsubscribe from
     * @param connection the client connection
     */
    private void unsubscribe(UUID jplId, WebSocketConnection connection) {
        Set<WebSocketConnection> subs = subscriptions.get(jplId);
        if (subs != null) {
            subs.remove(connection);
            if (subs.isEmpty()) {
                subscriptions.remove(jplId);
            }
        }
        LOG.infof("Connection %s unsubscribed from stream room for JPL %s", connection.id(), jplId);
    }

    /**
     * Broadcasts a message to all subscribers of a JPL, excluding the sender.
     *
     * @param jplId   the target JPL ID
     * @param message the raw JSON message to broadcast
     */
    public void broadcast(UUID jplId, String message) {
        broadcast(jplId, message, null);
    }

    /**
     * Broadcasts a message to all subscribers of a JPL, optionally excluding one
     * connection.
     *
     * @param jplId             the target JPL ID
     * @param message           the raw JSON message to broadcast
     * @param excludeConnection the connection to exclude from broadcast (e.g., the
     *                          sender)
     */
    public void broadcast(UUID jplId, String message, WebSocketConnection excludeConnection) {
        Set<WebSocketConnection> subs = subscriptions.get(jplId);
        if (subs != null && !subs.isEmpty()) {
            LOG.debugf("Broadcasting stream data for JPL %s to %d clients", jplId, subs.size());
            for (WebSocketConnection conn : subs) {
                if (excludeConnection == null || !conn.id().equals(excludeConnection.id())) {
                    try {
                        conn.sendTextAndAwait(message);
                    } catch (Exception e) {
                        LOG.errorf("Failed to send message to client %s: %s", conn.id(), e.getMessage());
                    }
                }
            }
        }
    }
}
