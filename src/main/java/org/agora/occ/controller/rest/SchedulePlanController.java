package org.agora.occ.controller.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.agora.occ.dto.common.PagedResult;
import org.agora.occ.dto.scheduleplan.request.SchedulePlanRequest;
import org.agora.occ.dto.scheduleplan.response.SchedulePlanResponse;
import org.agora.occ.enums.SchedulePlanType;
import org.agora.occ.service.JplSchedulePlanService;
import org.agora.occ.service.SchedulePlanService;
import org.agora.occ.service.StationSchedulePlanService;
import org.agora.occ.util.ApiResponse;
import org.jboss.logging.Logger;

import java.util.UUID;

@Path("/api/v1/schedule-plans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class SchedulePlanController {

    private static final Logger LOG = Logger.getLogger(SchedulePlanController.class);

    @Inject
    SchedulePlanService schedulePlanService;

    @Inject
    JplSchedulePlanService jplService;

    @Inject
    StationSchedulePlanService stationService;

    @POST
    public Response create(@Valid SchedulePlanRequest request,
            @QueryParam("type") SchedulePlanType type) {
        if (type == null) {
            return ApiResponse.error(Response.Status.BAD_REQUEST, "?type query parameter is required");
        }

        LOG.infov("Received request to create Schedule Plan of type: {0}", type);

        SchedulePlanResponse response;
        if (type == SchedulePlanType.JPL_SCHEDULE) {
            response = jplService.create(request);
        } else {
            response = stationService.create(request);
        }
        return ApiResponse.created(response, "Schedule Plan created successfully");
    }

    @GET
    public Response findAll(@QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("trainId") UUID trainId,
            @QueryParam("type") SchedulePlanType type,
            @QueryParam("date") String dateStr) {
        LOG.infov("Fetching Schedule Plans - page: {0}, size: {1}, type: {2}, date: {3}", page, size, type, dateStr);
        PagedResult<SchedulePlanResponse> results = schedulePlanService.findAll(trainId, dateStr, type, page, size);
        return ApiResponse.paginated(results, "Schedule Plans retrieved successfully");
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") UUID id) {
        LOG.infov("Fetching Schedule Plan with id: {0}", id);
        SchedulePlanResponse response = schedulePlanService.findById(id);
        return ApiResponse.ok(response, "Schedule Plan retrieved successfully");
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") UUID id, @Valid SchedulePlanRequest request) {
        LOG.infov("Updating Schedule Plan with id: {0}", id);
        SchedulePlanResponse response = schedulePlanService.update(id, request);
        return ApiResponse.ok(response, "Schedule Plan updated successfully");
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        LOG.infov("Deleting Schedule Plan with id: {0}", id);
        schedulePlanService.delete(id);
        return ApiResponse.ok(null, "Schedule Plan deleted successfully");
    }
}
