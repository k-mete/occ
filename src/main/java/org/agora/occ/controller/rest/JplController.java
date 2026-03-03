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
import org.agora.occ.dto.jpl.request.CreateJplRequest;
import org.agora.occ.enums.ActiveStatus;
import org.agora.occ.service.JplService;

import java.util.UUID;

@Path("/api/v1/jpls")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JplController {

    private final JplService jplService;

    @Inject
    public JplController(JplService jplService) {
        this.jplService = jplService;
    }

    @POST
    public Response create(@Valid CreateJplRequest request) {
        return ApiResponse.created(jplService.create(request), "JPL created successfully");
    }

    @GET
    public Response findAll(
            @QueryParam("stationId") UUID stationId,
            @QueryParam("occId") UUID occId,
            @QueryParam("status") ActiveStatus status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        return ApiResponse.paginated(jplService.findAll(stationId, occId, status, page, size),
                "JPLs retrieved successfully");
    }

    @GET
    @Path("/station/{stationId}")
    public Response findByStationId(@PathParam("stationId") UUID stationId) {
        return ApiResponse.ok(jplService.findByStationId(stationId), "JPLs retrieved successfully");
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") UUID id) {
        return ApiResponse.ok(jplService.findById(id), "JPL retrieved successfully");
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") UUID id, @Valid CreateJplRequest request) {
        return ApiResponse.ok(jplService.update(id, request), "JPL updated successfully");
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        jplService.delete(id);
        return ApiResponse.ok(null, "JPL deleted successfully");
    }
}
