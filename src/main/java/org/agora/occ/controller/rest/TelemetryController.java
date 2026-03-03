package org.agora.occ.controller.rest;

import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.agora.occ.util.ApiResponse;
import org.agora.occ.dto.telemetry.request.TelemetryRequest;
import org.agora.occ.service.IngestTelemetryService;
import org.jboss.logging.Logger;

@Path("/api/v1/telemetry")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TelemetryController {

    private static final Logger LOG = Logger.getLogger(TelemetryController.class);

    private final IngestTelemetryService ingestTelemetryService;

    public TelemetryController(IngestTelemetryService ingestTelemetryService) {
        this.ingestTelemetryService = ingestTelemetryService;
    }

    /**
     * Ingests telemetry data from trains.
     *
     * @param request the telemetry data
     * @return 200 OK on success
     */
    @POST
    public Response ingest(@Valid TelemetryRequest request) {
        LOG.infov("Received telemetry ingestion request for train: {0}", request.getTrainId());

        try {
            ingestTelemetryService.execute(request);
            return ApiResponse.ok(null, "Telemetry ingested successfully");
        } catch (IllegalArgumentException e) {
            LOG.warnv("Invalid argument in ingest telemetry: {0}", e.getMessage());
            return ApiResponse.error(Response.Status.BAD_REQUEST, e.getMessage());
        }
    }
}
