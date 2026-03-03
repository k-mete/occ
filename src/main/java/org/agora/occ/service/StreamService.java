package org.agora.occ.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.agora.occ.controller.websocket.LiveStreamWebSocket;
import org.agora.occ.dto.stream.response.StreamResponse;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Service for controlling live camera streams at a JPL.
 *
 * <p>
 * Uses the {@link LiveStreamWebSocket} to relay stream commands to the JPL
 * device
 * and any subscribed viewers.
 */
@ApplicationScoped
public class StreamService {

    private static final Logger LOG = Logger.getLogger(StreamService.class);

    private final LiveStreamWebSocket liveStreamWebSocket;
    private final ObjectMapper objectMapper;

    @Inject
    public StreamService(LiveStreamWebSocket liveStreamWebSocket, ObjectMapper objectMapper) {
        this.liveStreamWebSocket = liveStreamWebSocket;
        this.objectMapper = objectMapper;
    }

    /**
     * Requests the JPL device to start a stream.
     * Broadcasts a {@code stream_command} JSON payload to all subscribers for that
     * JPL.
     *
     * @param jplId      the ID of the JPL whose camera should start streaming
     * @param quality    the requested video quality (e.g. "low", "medium", "high")
     * @param ttlSeconds the viewer token time-to-live in seconds
     * @return a {@link StreamResponse} indicating success or error
     */
    public StreamResponse startStream(UUID jplId, String quality, Integer ttlSeconds) {
        LOG.infov("Requesting stream start for JPL {0}, quality={1}, ttl={2}", jplId, quality, ttlSeconds);
        try {
            String command = buildCommand("start_stream", jplId, quality, ttlSeconds);
            liveStreamWebSocket.broadcast(jplId, command);

            StreamResponse response = new StreamResponse();
            response.setType("ack");
            response.setMessageId(UUID.randomUUID());
            response.setJplId(jplId);
            response.setTs(Instant.now());
            response.setStatus("stream_start_requested");
            return response;
        } catch (Exception e) {
            LOG.errorv("Failed to start stream for JPL {0}: {1}", jplId, e.getMessage());
            StreamResponse error = new StreamResponse();
            error.setType("error");
            error.setJplId(jplId);
            error.setError(e.getMessage());
            return error;
        }
    }

    /**
     * Requests the JPL device to stop a stream.
     * Broadcasts a {@code stop_stream} JSON payload to all subscribers for that
     * JPL.
     *
     * @param jplId the ID of the JPL whose camera should stop streaming
     * @return a {@link StreamResponse} indicating success or error
     */
    public StreamResponse stopStream(UUID jplId) {
        LOG.infov("Requesting stream stop for JPL {0}", jplId);
        try {
            String command = buildCommand("stop_stream", jplId, null, null);
            liveStreamWebSocket.broadcast(jplId, command);

            StreamResponse response = new StreamResponse();
            response.setType("ack");
            response.setMessageId(UUID.randomUUID());
            response.setJplId(jplId);
            response.setTs(Instant.now());
            response.setStatus("stream_stop_requested");
            return response;
        } catch (Exception e) {
            LOG.errorv("Failed to stop stream for JPL {0}: {1}", jplId, e.getMessage());
            StreamResponse error = new StreamResponse();
            error.setType("error");
            error.setJplId(jplId);
            error.setError(e.getMessage());
            return error;
        }
    }

    /**
     * Builds a JSON command payload to relay via WebSocket.
     *
     * @param action     the action string (e.g. "start_stream" or "stop_stream")
     * @param jplId      the target JPL ID
     * @param quality    optional video quality
     * @param ttlSeconds optional token TTL
     * @return a serialized JSON string
     */
    private String buildCommand(String action, UUID jplId, String quality, Integer ttlSeconds) throws Exception {
        Map<String, Object> payload = new java.util.LinkedHashMap<>();
        payload.put("type", "stream_command");
        payload.put("action", action);
        payload.put("jplId", jplId.toString());
        payload.put("messageId", UUID.randomUUID().toString());
        payload.put("ts", Instant.now().toString());
        if (quality != null)
            payload.put("quality", quality);
        if (ttlSeconds != null)
            payload.put("ttlSeconds", ttlSeconds);
        return objectMapper.writeValueAsString(payload);
    }
}
