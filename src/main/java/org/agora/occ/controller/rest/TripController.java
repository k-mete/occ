package org.agora.occ.controller.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.agora.occ.dto.common.PagedResult;
import org.agora.occ.dto.trip.request.CreateTripRequest;
import org.agora.occ.dto.trip.request.RecordProgressRequest;
import org.agora.occ.dto.trip.response.TripDetailResponse;
import org.agora.occ.dto.trip.response.TripProgressResponse;
import org.agora.occ.dto.trip.response.TripResponse;
import org.agora.occ.service.TripService;
import org.agora.occ.util.ApiResponse;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;

/**
 * REST controller exposing trip management endpoints.
 *
 * <p>
 * Base path: {@code /api/v1/trips}
 * </p>
 */
@Path("/api/v1/trips")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class TripController {

    private static final Logger LOG = Logger.getLogger(TripController.class);

    private final TripService tripService;

    @Inject
    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    /**
     * Creates a new trip.
     *
     * @param request the create trip request payload
     * @return 201 with the created {@link TripResponse}
     */
    @POST
    public Response create(@Valid CreateTripRequest request) {
        LOG.infof("Received request to create trip for trainId=%s", request.getTrainId());
        TripResponse trip = tripService.create(request);
        return ApiResponse.created(trip, "Trip created successfully");
    }

    /**
     * Records a progress checkpoint for an existing trip.
     *
     * @param request the record progress request payload
     * @return 201 with the created {@link TripProgressResponse}
     */
    @POST
    @Path("/progress")
    public Response recordProgress(@Valid RecordProgressRequest request) {
        LOG.infof("Received request to record progress for tripId=%s", request.getTripId());
        TripProgressResponse progress = tripService.recordProgress(request);
        return ApiResponse.created(progress, "Trip progress recorded successfully");
    }

    /**
     * Retrieves trip history for a specific train.
     *
     * <p>
     * Returns an unpaginated list ordered by startTime descending.
     * </p>
     *
     * @param trainId the train UUID
     * @return unpaginated list of {@link TripResponse}
     */
    @GET
    @Path("/trains/{trainId}")
    public Response getHistoryByTrain(@PathParam("trainId") UUID trainId) {
        LOG.infof("Received request to fetch trip history for trainId=%s", trainId);
        List<TripResponse> history = tripService.getHistoryByTrain(trainId);
        return ApiResponse.ok(history, "Trip history retrieved successfully");
    }

    /**
     * Retrieves a paginated or full list of trips with optional filters.
     *
     * <p>
     * Supported query parameters:
     * <ul>
     * <li>{@code trainId} — filter by train UUID</li>
     * <li>{@code routeId} — filter by route UUID</li>
     * <li>{@code isFlow} — filter by direction (true = departure, false =
     * return)</li>
     * <li>{@code dateFrom} — start date filter in dd/MM/yyyy</li>
     * <li>{@code dateTo} — end date filter in dd/MM/yyyy</li>
     * <li>{@code page} — zero-based page index (default: 0)</li>
     * <li>{@code size} — page size (default: 10, use -1 for all)</li>
     * </ul>
     * </p>
     *
     * @return paginated or full list of {@link TripResponse}
     */
    @GET
    public Response getAllTrips(
            @QueryParam("trainId") UUID trainId,
            @QueryParam("routeId") UUID routeId,
            @QueryParam("isFlow") Boolean isFlow,
            @QueryParam("dateFrom") String dateFrom,
            @QueryParam("dateTo") String dateTo,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {

        LOG.infof("Received request to list trips with filters trainId=%s, routeId=%s, isFlow=%s, size=%d",
                trainId, routeId, isFlow, size);

        PagedResult<TripResponse> result = tripService.getAllTrips(
                trainId, routeId, isFlow, dateFrom, dateTo, page, size);

        if (size == -1) {
            return ApiResponse.ok(result.getData(), "Trips retrieved successfully");
        }
        return ApiResponse.paginated(result, "Trips retrieved successfully");
    }

    /**
     * Retrieves a specific trip with all its progress checkpoints.
     *
     * @param id the trip UUID
     * @return {@link TripDetailResponse} including the embedded progress list
     */
    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") UUID id) {
        LOG.infof("Received request to fetch trip with id=%s", id);
        TripDetailResponse detail = tripService.getById(id);
        return ApiResponse.ok(detail, "Trip retrieved successfully");
    }
}
