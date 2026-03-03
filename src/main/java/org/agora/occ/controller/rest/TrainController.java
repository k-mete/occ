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
import org.agora.occ.dto.train.request.CreateTrainRequest;
import org.agora.occ.dto.train.response.TrainResponse;
import org.agora.occ.enums.ActiveStatus;
import org.agora.occ.enums.TransportCategory;
import org.agora.occ.service.TrainService;
import org.agora.occ.util.ApiResponse;
import org.jboss.logging.Logger;

import java.util.UUID;

@Path("/api/v1/trains")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class TrainController {

    private static final Logger LOG = Logger.getLogger(TrainController.class);

    @Inject
    TrainService trainService;

    @POST
    public Response create(@Valid CreateTrainRequest request) {
        LOG.infov("Received request to create train: {0}", request.getTrainName());
        TrainResponse response = trainService.create(request);
        return ApiResponse.created(response, "Train created successfully");
    }

    @GET
    public Response findAll(@QueryParam("category") TransportCategory category,
            @QueryParam("status") ActiveStatus status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        LOG.infov("Fetching trains - category: {0}, status: {1}", category, status);
        PagedResult<TrainResponse> results = trainService.findAll(category, status, page, size);
        return ApiResponse.paginated(results, "Trains retrieved successfully");
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") UUID id) {
        LOG.infov("Fetching train with id: {0}", id);
        TrainResponse response = trainService.findById(id);
        return ApiResponse.ok(response, "Train retrieved successfully");
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") UUID id, @Valid CreateTrainRequest request) {
        LOG.infov("Updating train with id: {0}", id);
        TrainResponse response = trainService.update(id, request);
        return ApiResponse.ok(response, "Train updated successfully");
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") UUID id) {
        LOG.infov("Deleting train with id: {0}", id);
        trainService.delete(id);
        return ApiResponse.ok(null, "Train deleted successfully");
    }
}
