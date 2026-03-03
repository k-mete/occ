package org.agora.occ.controller.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.agora.occ.dto.health.HealthDetailResponse;
import org.agora.occ.dto.health.HealthItemResponse;
import org.agora.occ.dto.health.HealthSummaryResponse;
import org.agora.occ.enums.ConnectionStatus;
import org.agora.occ.enums.HealthCategory;
import org.agora.occ.service.HealthService;
import org.agora.occ.util.ApiResponse;

import java.util.List;
import java.util.UUID;

/**
 * Controller exposing real-time WebSocket connection health.
 */
@Path("/api/v1/health")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class HealthController {

    private final HealthService healthService;

    @Inject
    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    /**
     * Returns an aggregate summary of online/offline counts across all categories.
     *
     * @return {@link HealthSummaryResponse}
     */
    @GET
    @Path("/summary")
    public Response getOverallSummary() {
        HealthSummaryResponse summary = healthService.getOverallSummary();
        return ApiResponse.ok(summary, "Health summary retrieved successfully");
    }

    /**
     * Lists all tracked entities in a specific category.
     *
     * @param categoryName jpl, train, or station
     * @param statusRaw    optional filter: ONLINE or OFFLINE
     * @return unpaginated list of {@link HealthItemResponse}
     */
    @GET
    @Path("/{category}")
    public Response getCategoryList(
            @PathParam("category") String categoryName,
            @QueryParam("status") String statusRaw) {

        HealthCategory category = parseCategory(categoryName);
        if (category == null) {
            return ApiResponse.error(Response.Status.BAD_REQUEST,
                    "Invalid category. Must be 'jpl', 'train', or 'station'.");
        }

        ConnectionStatus status = null;
        if (statusRaw != null) {
            try {
                status = ConnectionStatus.valueOf(statusRaw.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ApiResponse.error(Response.Status.BAD_REQUEST, "Invalid status. Must be 'ONLINE' or 'OFFLINE'.");
            }
        }

        List<HealthItemResponse> results = healthService.getListByStatus(category, status);
        return ApiResponse.ok(results, "Health list retrieved successfully");
    }

    /**
     * Retrieves detailed health state for a single specific entity ID.
     *
     * @param categoryName jpl, train, or station
     * @param id           the tracked UUID
     * @return {@link HealthDetailResponse}
     */
    @GET
    @Path("/{category}/{id}")
    public Response getEntityById(
            @PathParam("category") String categoryName,
            @PathParam("id") UUID id) {

        HealthCategory category = parseCategory(categoryName);
        if (category == null) {
            return ApiResponse.error(Response.Status.BAD_REQUEST,
                    "Invalid category. Must be 'jpl', 'train', or 'station'.");
        }

        HealthDetailResponse detail = healthService.getEntityHealth(category, id);
        return ApiResponse.ok(detail, "Entity health retrieved successfully");
    }

    private HealthCategory parseCategory(String raw) {
        if (raw == null)
            return null;
        try {
            return HealthCategory.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
