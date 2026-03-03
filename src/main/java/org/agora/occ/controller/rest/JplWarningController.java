package org.agora.occ.controller.rest;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.agora.occ.util.ApiResponse;
import org.agora.occ.dto.warning.request.WarningRequest;
import org.agora.occ.service.BroadcastWarningService;
import org.jboss.logging.Logger;

@Path("/api/v1/jpl/warnings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JplWarningController {

    private static final Logger LOG = Logger.getLogger(JplWarningController.class);

    private final BroadcastWarningService broadcastWarningService;

    public JplWarningController(BroadcastWarningService broadcastWarningService) {
        this.broadcastWarningService = broadcastWarningService;
    }

    /**
     * Receives and broadcasts a warning message.
     *
     * @param request the warning request
     * @return 200 OK on success
     */
    @POST
    public Response receiveWarning(@Valid WarningRequest request) {
        LOG.infov("Received warning broadcast request for JPL ID: {0}", request.getJplId());
        broadcastWarningService.broadcastWarning(request);
        return ApiResponse.ok(null, "Warning broadcasted successfully");
    }
}
