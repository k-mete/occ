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
import org.agora.occ.dto.occ.request.CreateOccRequest;
import org.agora.occ.service.OccService;

import java.util.UUID;

@Path("/api/v1/occs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OccController {

    private final OccService occService;

    @Inject
    public OccController(OccService occService) {
        this.occService = occService;
    }

    @POST
    public Response create(@Valid CreateOccRequest request) {
        return ApiResponse.created(occService.create(request), "OCC created successfully");
    }

    @GET
    public Response findAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        return ApiResponse.paginated(occService.findAll(page, size), "OCCs retrieved successfully");
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") UUID id) {
        return ApiResponse.ok(occService.findById(id), "OCC retrieved successfully");
    }

    @GET
    @Path("/{id}/detail")
    public Response getDetail(@PathParam("id") UUID id) {
        return ApiResponse.ok(occService.getDetail(id), "OCC details retrieved successfully");
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") UUID id, @Valid CreateOccRequest request) {
        return ApiResponse.ok(occService.update(id, request), "OCC updated successfully");
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        occService.delete(id);
        return ApiResponse.ok(null, "OCC deleted successfully");
    }
}
