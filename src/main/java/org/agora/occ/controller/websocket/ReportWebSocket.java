package org.agora.occ.controller.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.agora.occ.dto.websocket.ReportWebSocketMessage;
import org.agora.occ.entity.event.ReportCreatedEvent;
import org.agora.occ.service.ReportService;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket handler for real-time report notifications.
 *
 * <p>
 * All connected clients receive every report broadcast. Clients
 * can send a {@code markAsRead} action for a list of report IDs.
 *
 * <p>
 * New reports are pushed automatically when a {@link ReportCreatedEvent}
 * CDI event is fired from the service layer.
 */
@WebSocket(path = "/ws/reports")
@ApplicationScoped
public class ReportWebSocket {

    private static final Logger LOG = Logger.getLogger(ReportWebSocket.class);

    private final ObjectMapper objectMapper;
    private final ReportService reportService;

    /** Set of all currently connected clients. */
    private final Set<WebSocketConnection> connections = ConcurrentHashMap.newKeySet();

    /**
     * Creates a new ReportWebSocket with the required dependencies.
     *
     * @param objectMapper  the Jackson object mapper for JSON serialization
     * @param reportService the report service for persisting read status
     */
    @Inject
    public ReportWebSocket(ObjectMapper objectMapper, ReportService reportService) {
        this.objectMapper = objectMapper;
        this.reportService = reportService;
    }

    /**
     * Tracks a new client connection.
     *
     * @param connection the newly opened WebSocket connection
     */
    @OnOpen
    public void onOpen(WebSocketConnection connection) {
        connections.add(connection);
        LOG.infof("New Report WebSocket connection: %s", connection.id());
    }

    /**
     * Removes the connection from the tracked set on disconnect.
     *
     * @param connection the closed WebSocket connection
     */
    @OnClose
    public void onClose(WebSocketConnection connection) {
        connections.remove(connection);
        LOG.infof("Report WebSocket connection closed: %s", connection.id());
    }

    /**
     * Handles incoming messages from clients.
     * Supports the {@code markAsRead} action with a list of report IDs.
     *
     * @param message    the raw JSON message text
     * @param connection the sender's WebSocket connection
     */
    @OnTextMessage
    public void onMessage(String message, WebSocketConnection connection) {
        try {
            ReportWebSocketMessage request = objectMapper.readValue(message, ReportWebSocketMessage.class);

            if ("markAsRead".equals(request.getAction()) && request.getIds() != null
                    && !request.getIds().isEmpty()) {
                handleMarkAsRead(request.getIds());
            }
        } catch (Exception e) {
            LOG.errorf("Failed to process message from %s: %s", connection.id(), e.getMessage());
        }
    }

    /**
     * Processes a markAsRead action and broadcasts the updated status to all
     * clients.
     *
     * @param ids the list of report IDs to mark as read
     */
    private void handleMarkAsRead(List<UUID> ids) {
        try {
            reportService.markAsRead(ids);
            ReportWebSocketMessage response = ReportWebSocketMessage.builder()
                    .action("statusUpdate")
                    .ids(ids)
                    .isRead(true)
                    .build();
            broadcastToAll(objectMapper.writeValueAsString(response));
            LOG.debugf("Broadcasted markAsRead update for %d reports", ids.size());
        } catch (Exception e) {
            LOG.errorf("Failed to broadcast markAsRead update: %s", e.getMessage());
        }
    }

    /**
     * Observes CDI {@link ReportCreatedEvent} and pushes a {@code newReport}
     * notification to all connected clients.
     *
     * @param event the created report event fired from the service layer
     */
    public void onReportCreated(@Observes ReportCreatedEvent event) {
        try {
            ReportWebSocketMessage message = ReportWebSocketMessage.builder()
                    .action("newReport")
                    .data(event)
                    .build();
            broadcastToAll(objectMapper.writeValueAsString(message));
            LOG.debugf("Broadcasted new report %s to %d clients", event.id(), connections.size());
        } catch (Exception e) {
            LOG.errorf("Failed to broadcast new report: %s", e.getMessage());
        }
    }

    /**
     * Sends a message to every connected client.
     *
     * @param message the JSON string to broadcast
     */
    private void broadcastToAll(String message) {
        for (WebSocketConnection connection : connections) {
            try {
                connection.sendTextAndAwait(message);
            } catch (Exception e) {
                LOG.errorf("Failed to send message to client %s: %s", connection.id(), e.getMessage());
            }
        }
    }
}
