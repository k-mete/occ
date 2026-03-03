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
import org.agora.occ.dto.route.request.CreateRouteRequest;
import org.agora.occ.dto.route.response.RouteDetailResponse;
import org.agora.occ.dto.route.response.RouteResponse;
import org.agora.occ.enums.TransportCategory;
import org.agora.occ.service.RouteService;
import org.agora.occ.util.ApiResponse;
import org.jboss.logging.Logger;

import java.util.UUID;

@Path("/api/v1/routes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class RouteController {

    private static final Logger LOG = Logger.getLogger(RouteController.class);

    @Inject
    RouteService routeService;

    @POST
    public Response create(@Valid CreateRouteRequest request) {
        LOG.infov("Received request to create route: {0}", request.getRouteCode());
        RouteDetailResponse response = routeService.create(request);
        return ApiResponse.created(response, "Route created successfully");
    }

    @GET
    public Response findAll(@QueryParam("category") TransportCategory category,
            @QueryParam("isActive") Boolean isActive,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        LOG.infov("Fetching routes - category: {0}, isActive: {1}", category, isActive);
        PagedResult<RouteResponse> results = routeService.findAll(category, isActive, page, size);
        return ApiResponse.paginated(results, "Routes retrieved successfully");
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") UUID id) {
        LOG.infov("Fetching route with id: {0}", id);
        RouteResponse response = routeService.findById(id);
        return ApiResponse.ok(response, "Route retrieved successfully");
    }

    @GET
    @Path("/{id}/detail")
    public Response getDetail(@PathParam("id") UUID id) {
        LOG.infov("Fetching route detail with id: {0}", id);
        RouteDetailResponse response = routeService.getDetail(id);
        return ApiResponse.ok(response, "Route detail retrieved successfully");
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") UUID id, @Valid CreateRouteRequest request) {
        LOG.infov("Updating route with id: {0}", id);
        RouteDetailResponse response = routeService.update(id, request);
        return ApiResponse.ok(response, "Route updated successfully");
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        LOG.infov("Deleting route with id: {0}", id);
        routeService.delete(id);
        return ApiResponse.ok(null, "Route deleted successfully");
    }
}
