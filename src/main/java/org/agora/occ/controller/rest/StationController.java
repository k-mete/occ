package org.agora.occ.controller.rest;

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
import org.agora.occ.util.ApiResponse;
import org.agora.occ.dto.station.request.CreateStationRequest;
import org.agora.occ.enums.ActiveStatus;
import org.agora.occ.service.StationService;

import java.util.UUID;

@Path("/api/v1/stations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StationController {

    private final StationService stationService;

    @Inject
    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @POST
    public Response create(@Valid CreateStationRequest request) {
        return ApiResponse.created(stationService.create(request), "Station created successfully");
    }

    @GET
    public Response findAll(
            @QueryParam("occId") UUID occId,
            @QueryParam("status") ActiveStatus status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        return ApiResponse.paginated(stationService.findAll(occId, status, page, size),
                "Stations retrieved successfully");
    }

    @GET
    @Path("/occ/{occId}")
    public Response findByOccId(@PathParam("occId") UUID occId) {
        return ApiResponse.ok(stationService.findByOccId(occId), "Stations retrieved successfully");
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") UUID id) {
        return ApiResponse.ok(stationService.findById(id), "Station retrieved successfully");
    }

    @GET
    @Path("/{id}/detail")
    public Response getDetail(@PathParam("id") UUID id) {
        return ApiResponse.ok(stationService.getDetail(id), "Station details retrieved successfully");
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") UUID id, @Valid CreateStationRequest request) {
        return ApiResponse.ok(stationService.update(id, request), "Station updated successfully");
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        stationService.delete(id);
        return ApiResponse.ok(null, "Station deleted successfully");
    }
}
