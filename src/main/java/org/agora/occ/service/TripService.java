package org.agora.occ.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.agora.occ.dto.common.PagedResult;
import org.agora.occ.dto.trip.request.CreateTripRequest;
import org.agora.occ.dto.trip.request.RecordProgressRequest;
import org.agora.occ.dto.trip.response.TripDetailResponse;
import org.agora.occ.dto.trip.response.TripProgressResponse;
import org.agora.occ.dto.trip.response.TripResponse;
import org.agora.occ.entity.TripEntity;
import org.agora.occ.entity.TripProgressEntity;
import org.agora.occ.mapper.TripMapper;
import org.agora.occ.repository.TripProgressRepository;
import org.agora.occ.repository.TripRepository;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service encapsulating all Trip business logic.
 */
@ApplicationScoped
public class TripService {

    private static final Logger LOG = Logger.getLogger(TripService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final TripRepository tripRepository;
    private final TripProgressRepository tripProgressRepository;
    private final TripMapper tripMapper;

    @Inject
    public TripService(TripRepository tripRepository,
            TripProgressRepository tripProgressRepository,
            TripMapper tripMapper) {
        this.tripRepository = tripRepository;
        this.tripProgressRepository = tripProgressRepository;
        this.tripMapper = tripMapper;
    }

    /**
     * Creates a new trip and persists it to the database.
     *
     * @param request the create trip request
     * @return the created trip response DTO
     */
    @Transactional
    public TripResponse create(CreateTripRequest request) {
        LOG.debugv("Creating trip for trainId={0}, routeId={1}", request.getTrainId(), request.getRouteId());
        TripEntity entity = tripMapper.toEntity(request);
        tripRepository.persist(entity);
        LOG.debugv("Trip created with id={0}", entity.getId());
        return tripMapper.toResponse(entity);
    }

    /**
     * Records a progress checkpoint for an existing trip.
     *
     * @param request the record progress request
     * @return the created progress response DTO
     * @throws NotFoundException if the referenced trip does not exist
     */
    @Transactional
    public TripProgressResponse recordProgress(RecordProgressRequest request) {
        LOG.debugv("Recording progress for tripId={0}", request.getTripId());
        validateTripExists(request.getTripId());
        TripProgressEntity entity = tripMapper.toProgressEntity(request);
        tripProgressRepository.persist(entity);
        LOG.debugv("Progress recorded with id={0}", entity.getId());
        return tripMapper.toProgressResponse(entity);
    }

    /**
     * Retrieves a full trip detail including all progress records for a given ID.
     *
     * @param id the trip UUID
     * @return the trip detail response with embedded progress list
     * @throws NotFoundException if the trip does not exist
     */
    public TripDetailResponse getById(UUID id) {
        LOG.debugv("Fetching trip by id={0}", id);
        TripEntity trip = findTripOrThrow(id);
        List<TripProgressEntity> progressList = tripProgressRepository.findByTripId(id);
        return tripMapper.toDetailResponse(trip, progressList);
    }

    /**
     * Retrieves a filtered and paginated list of trips.
     *
     * <p>
     * If {@code size} is -1, all matching trips are returned without pagination.
     * </p>
     * <p>
     * Dates should be supplied in {@code dd/MM/yyyy} format.
     * </p>
     *
     * @param trainId     optional filter by train UUID
     * @param routeId     optional filter by route UUID
     * @param isFlow      optional filter by trip direction
     * @param dateFromStr optional lower date bound in dd/MM/yyyy format
     * @param dateToStr   optional upper date bound in dd/MM/yyyy format
     * @param page        zero-based page index
     * @param size        items per page; -1 returns all
     * @return paginated or full list of trip responses
     */
    public PagedResult<TripResponse> getAllTrips(UUID trainId, UUID routeId, Boolean isFlow,
            String dateFromStr, String dateToStr,
            int page, int size) {
        Instant from = parseDate(dateFromStr, true);
        Instant to = parseDate(dateToStr, false);

        if (size == -1) {
            return fetchAllUnpaginated(trainId, routeId, isFlow, from, to);
        }
        return fetchPaginated(trainId, routeId, isFlow, from, to, page, size);
    }

    /**
     * Retrieves the full trip history for a specific train, ordered by start time
     * descending.
     *
     * @param trainId the train UUID
     * @return unpaginated list of trip responses
     */
    public List<TripResponse> getHistoryByTrain(UUID trainId) {
        LOG.debugv("Fetching trip history for trainId={0}", trainId);
        return tripRepository.findByTrainId(trainId).stream()
                .map(tripMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Validates that a trip with the given ID exists.
     *
     * @param tripId the trip UUID to validate
     * @throws NotFoundException if no trip is found
     */
    private void validateTripExists(UUID tripId) {
        if (tripRepository.findByIdOptional(tripId).isEmpty()) {
            throw new NotFoundException("Trip not found with id: " + tripId);
        }
    }

    /**
     * Finds a trip by ID or throws a NotFoundException.
     *
     * @param id the trip UUID
     * @return the TripEntity
     * @throws NotFoundException if no trip is found
     */
    private TripEntity findTripOrThrow(UUID id) {
        return tripRepository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Trip not found with id: " + id));
    }

    /**
     * Returns all matching trips as a single-page result without pagination.
     *
     * @param trainId optional trainId filter
     * @param routeId optional routeId filter
     * @param isFlow  optional direction filter
     * @param from    optional startTime lower bound
     * @param to      optional startTime upper bound
     * @return paged result with all elements on a single page
     */
    private PagedResult<TripResponse> fetchAllUnpaginated(UUID trainId, UUID routeId, Boolean isFlow,
            Instant from, Instant to) {
        long total = tripRepository.countFiltered(trainId, routeId, isFlow, from, to);
        List<TripResponse> data = tripRepository
                .findFiltered(trainId, routeId, isFlow, from, to, 0, (int) total)
                .stream()
                .map(tripMapper::toResponse)
                .collect(Collectors.toList());
        return new PagedResult<>(data, 0, (int) total, total);
    }

    /**
     * Returns a single paginated page of matching trips.
     *
     * @param trainId optional trainId filter
     * @param routeId optional routeId filter
     * @param isFlow  optional direction filter
     * @param from    optional startTime lower bound
     * @param to      optional startTime upper bound
     * @param page    zero-based page index
     * @param size    items per page
     * @return the paged result DTO
     */
    private PagedResult<TripResponse> fetchPaginated(UUID trainId, UUID routeId, Boolean isFlow,
            Instant from, Instant to, int page, int size) {
        long total = tripRepository.countFiltered(trainId, routeId, isFlow, from, to);
        List<TripResponse> data = tripRepository
                .findFiltered(trainId, routeId, isFlow, from, to, page, size)
                .stream()
                .map(tripMapper::toResponse)
                .collect(Collectors.toList());
        return new PagedResult<>(data, page, size, total);
    }

    /**
     * Parses a date string in {@code dd/MM/yyyy} format to an {@link Instant}.
     *
     * @param dateStr    the date string to parse, or {@code null}
     * @param startOfDay if {@code true}, returns start of day (00:00:00); otherwise
     *                   end of day (23:59:59)
     * @return the parsed Instant, or {@code null} if input is null or invalid
     */
    private Instant parseDate(String dateStr, boolean startOfDay) {
        if (dateStr == null || dateStr.isBlank())
            return null;
        try {
            LocalDate date = LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
            return startOfDay
                    ? date.atStartOfDay(ZoneOffset.UTC).toInstant()
                    : date.atTime(23, 59, 59).toInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException e) {
            LOG.warnv("Invalid date format provided: {0}", dateStr);
            return null;
        }
    }
}
