package org.agora.occ.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.dto.jpl.response.JplResponse;
import org.agora.occ.dto.station.request.CreateStationRequest;
import org.agora.occ.dto.station.response.StationDetailResponse;
import org.agora.occ.dto.station.response.StationResponse;
import org.agora.occ.entity.OccEntity;
import org.agora.occ.entity.StationEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class StationMapper {

    /**
     * Converts a request to a new StationEntity.
     */
    public StationEntity toEntity(CreateStationRequest request) {
        return StationEntity.builder()
                .id(UUID.randomUUID())
                .stationName(request.getStationName())
                .stationCode(request.getStationCode())
                .stationAddress(request.getStationAddress())
                .stationLatitude(request.getStationLatitude())
                .stationLongitude(request.getStationLongitude())
                .stationStatus(request.getStationStatus())
                .heading(request.getHeading())
                .occ(OccEntity.builder().id(request.getOccId()).build())
                .stationIndex(request.getStationIndex())
                .createdAt(Instant.now())
                .build();
    }

    /**
     * Converts a StationEntity to a standard StationResponse.
     */
    public StationResponse toResponse(StationEntity entity) {
        return StationResponse.builder()
                .id(entity.getId())
                .stationName(entity.getStationName())
                .stationCode(entity.getStationCode())
                .stationAddress(entity.getStationAddress())
                .stationLatitude(entity.getStationLatitude())
                .stationLongitude(entity.getStationLongitude())
                .stationStatus(entity.getStationStatus())
                .heading(entity.getHeading())
                .occId(entity.getOcc() != null ? entity.getOcc().getId() : null)
                .occName(entity.getOcc() != null ? entity.getOcc().getOccName() : null)
                .stationIndex(entity.getStationIndex())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Converts a StationEntity and its nested Jpls to a StationDetailResponse.
     */
    public StationDetailResponse toDetailResponse(StationEntity entity, List<JplResponse> jpls) {
        return StationDetailResponse.builder()
                .id(entity.getId())
                .stationName(entity.getStationName())
                .stationCode(entity.getStationCode())
                .stationAddress(entity.getStationAddress())
                .stationLatitude(entity.getStationLatitude())
                .stationLongitude(entity.getStationLongitude())
                .stationStatus(entity.getStationStatus())
                .heading(entity.getHeading())
                .occId(entity.getOcc() != null ? entity.getOcc().getId() : null)
                .occName(entity.getOcc() != null ? entity.getOcc().getOccName() : null)
                .stationIndex(entity.getStationIndex())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .jpls(jpls)
                .build();
    }

    /**
     * Updates an existing StationEntity in place from a request.
     */
    public void updateEntity(StationEntity entity, CreateStationRequest request) {
        if (request.getStationName() != null) {
            entity.setStationName(request.getStationName());
        }
        if (request.getStationCode() != null) {
            entity.setStationCode(request.getStationCode());
        }
        if (request.getStationAddress() != null) {
            entity.setStationAddress(request.getStationAddress());
        }
        if (request.getStationLatitude() != null) {
            entity.setStationLatitude(request.getStationLatitude());
        }
        if (request.getStationLongitude() != null) {
            entity.setStationLongitude(request.getStationLongitude());
        }
        if (request.getStationStatus() != null) {
            entity.setStationStatus(request.getStationStatus());
        }
        if (request.getHeading() != null) {
            entity.setHeading(request.getHeading());
        }
        if (request.getOccId() != null) {
            entity.setOcc(OccEntity.builder().id(request.getOccId()).build());
        }
        if (request.getStationIndex() != null) {
            entity.setStationIndex(request.getStationIndex());
        }
    }
}
