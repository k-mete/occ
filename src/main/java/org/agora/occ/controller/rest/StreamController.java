package org.agora.occ.controller.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.agora.occ.dto.stream.request.StreamRequest;
import org.agora.occ.dto.stream.response.StreamResponse;
import org.agora.occ.service.StreamService;
import org.agora.occ.util.ApiResponse;
import org.jboss.logging.Logger;

/**
 * REST resource for controlling JPL camera live streams.
 *
 * <p>
 * Calls relay commands to connected JPL devices via the
 * {@link org.agora.occ.controller.websocket.LiveStreamWebSocket} by delegating
 * to
 * {@link StreamService}.
 */
@Path("/api/v1/stream")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StreamController {

    private static final Logger LOG = Logger.getLogger(StreamController.class);

    private final StreamService streamService;

    @Inject
    public StreamController(StreamService streamService) {
        this.streamService = streamService;
    }

    /**
     * Starts a stream for a specific JPL.
     * Issues a start command so the JPL device begins streaming and notifies
     * subscribers.
     *
     * @param request the stream request (jplId required; quality and ttlSeconds
     *                optional)
     * @return 200 OK with an ack payload on success, or 500 on error
     */
    @POST
    @Path("/start")
    public Response startStream(@Valid StreamRequest request) {
        LOG.infov("Received request to start stream for JPL ID: {0}", request.getJplId());
        StreamResponse response = streamService.startStream(
                request.getJplId(), request.getQuality(), request.getTtlSeconds());

        if ("error".equals(response.getType())) {
            LOG.errorv("Failed to start stream for JPL {0}: {1}", request.getJplId(), response.getError());
            return ApiResponse.error(Response.Status.INTERNAL_SERVER_ERROR, response.getError());
        }
        return ApiResponse.ok(response, "Stream start command sent successfully");
    }

    /**
     * Stops a stream for a specific JPL.
     * Issues a stop command so the JPL device halts streaming and notifies
     * subscribers.
     *
     * @param request the stream request containing the JPL ID
     * @return 200 OK with an ack payload on success, or 400/500 on error
     */
    @POST
    @Path("/stop")
    public Response stopStream(@Valid StreamRequest request) {
        LOG.infov("Received request to stop stream for JPL ID: {0}", request.getJplId());
        StreamResponse response = streamService.stopStream(request.getJplId());

        if ("error".equals(response.getType())) {
            LOG.errorv("Failed to stop stream for JPL {0}: {1}", request.getJplId(), response.getError());
            return ApiResponse.error(Response.Status.INTERNAL_SERVER_ERROR, response.getError());
        }
        return ApiResponse.ok(response, "Stream stop command sent successfully");
    }
}
