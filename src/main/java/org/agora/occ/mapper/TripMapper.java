package org.agora.occ.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.dto.trip.request.CreateTripRequest;
import org.agora.occ.dto.trip.request.RecordProgressRequest;
import org.agora.occ.dto.trip.response.TripDetailResponse;
import org.agora.occ.dto.trip.response.TripProgressResponse;
import org.agora.occ.dto.trip.response.TripResponse;
import org.agora.occ.entity.TripEntity;
import org.agora.occ.entity.TripProgressEntity;
import org.agora.occ.entity.JplEntity;
import org.agora.occ.entity.StationEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Trip entities and DTOs.
 */
@ApplicationScoped
public class TripMapper {

    /**
     * Converts a {@link TripEntity} to a {@link TripResponse}.
     *
     * @param entity the trip entity
     * @return the trip response DTO
     */
    public TripResponse toResponse(TripEntity entity) {
        return TripResponse.builder()
                .id(entity.getId())
                .trainId(entity.getTrainId())
                .routeId(entity.getRouteId())
                .isFlow(entity.getIsFlow())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    /**
     * Converts a {@link TripProgressEntity} to a {@link TripProgressResponse}.
     *
     * @param entity the trip progress entity
     * @return the progress response DTO
     */
    public TripProgressResponse toProgressResponse(TripProgressEntity entity) {
        return TripProgressResponse.builder()
                .id(entity.getId())
                .tripId(entity.getTripId())
                .jplId(entity.getJpl() != null ? entity.getJpl().getId() : null)
                .stationId(entity.getStation() != null ? entity.getStation().getId() : null)
                .timestamp(entity.getTimestamp())
                .build();
    }

    /**
     * Converts a {@link TripEntity} and its associated progress records into a
     * {@link TripDetailResponse}.
     *
     * @param entity           the trip entity
     * @param progressEntities the ordered list of progress checkpoints
     * @return the enriched detail response DTO
     */
    public TripDetailResponse toDetailResponse(TripEntity entity, List<TripProgressEntity> progressEntities) {
        List<TripProgressResponse> progressList = progressEntities.stream()
                .map(this::toProgressResponse)
                .collect(Collectors.toList());

        return TripDetailResponse.builder()
                .id(entity.getId())
                .trainId(entity.getTrainId())
                .routeId(entity.getRouteId())
                .isFlow(entity.getIsFlow())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .createdAt(entity.getCreatedAt())
                .progress(progressList)
                .build();
    }

    /**
     * Converts a {@link CreateTripRequest} to a new {@link TripEntity}.
     *
     * <p>
     * Generates a fresh UUID and sets {@code createdAt} to now.
     * </p>
     *
     * @param request the create trip request
     * @return the trip entity ready to be persisted
     */
    public TripEntity toEntity(CreateTripRequest request) {
        return TripEntity.builder()
                .id(UUID.randomUUID())
                .trainId(request.getTrainId())
                .routeId(request.getRouteId())
                .isFlow(request.getIsFlow())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .createdAt(Instant.now())
                .build();
    }

    /**
     * Converts a {@link RecordProgressRequest} to a new {@link TripProgressEntity}.
     *
     * <p>
     * Generates a fresh UUID. Defaults {@code timestamp} to now if not provided.
     * </p>
     *
     * @param request the record progress request
     * @return the progress entity ready to be persisted
     */
    public TripProgressEntity toProgressEntity(RecordProgressRequest request) {
        return TripProgressEntity.builder()
                .id(UUID.randomUUID())
                .tripId(request.getTripId())
                .jpl(request.getJplId() != null ? JplEntity.builder().id(request.getJplId()).build() : null)
                .station(request.getStationId() != null ? StationEntity.builder().id(request.getStationId()).build()
                        : null)
                .timestamp(request.getTimestamp() != null ? request.getTimestamp() : Instant.now())
                .build();
    }
}
