package org.agora.occ.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.dto.jpl.request.CreateJplRequest;
import org.agora.occ.dto.jpl.response.JplResponse;
import org.agora.occ.entity.JplEntity;
import org.agora.occ.entity.StationEntity;

import java.time.Instant;
import java.util.UUID;

@ApplicationScoped
public class JplMapper {

    /**
     * Converts a request to a new JplEntity.
     *
     * @param request the create JPL request
     * @return a newly created JplEntity
     */
    public JplEntity toEntity(CreateJplRequest request) {
        return JplEntity.builder()
                .id(UUID.randomUUID())
                .jplName(request.getJplName())
                .jplAddress(request.getJplAddress())
                .jplStatus(request.getJplStatus())
                .station(StationEntity.builder().id(request.getStationId()).build())
                .jplLatitude(request.getJplLatitude())
                .jplLongitude(request.getJplLongitude())
                .heading(request.getHeading() != null ? request.getHeading() : 0)
                .createdAt(Instant.now())
                .build();
    }

    /**
     * Converts a JplEntity to a standard JplResponse.
     *
     * @param entity the JPL entity to convert
     * @return the JPL response DTO
     */
    public JplResponse toResponse(JplEntity entity) {
        return JplResponse.builder()
                .id(entity.getId())
                .jplName(entity.getJplName())
                .jplAddress(entity.getJplAddress())
                .jplStatus(entity.getJplStatus())
                .stationId(entity.getStation() != null ? entity.getStation().getId() : null)
                .stationName(entity.getStation() != null ? entity.getStation().getStationName() : null)
                .jplLatitude(entity.getJplLatitude())
                .jplLongitude(entity.getJplLongitude())
                .heading(entity.getHeading())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Updates an existing JplEntity in place from a request.
     *
     * @param entity  the entity to update
     * @param request the update request payload
     */
    public void updateEntity(JplEntity entity, CreateJplRequest request) {
        if (request.getJplName() != null)
            entity.setJplName(request.getJplName());
        if (request.getJplAddress() != null)
            entity.setJplAddress(request.getJplAddress());
        if (request.getJplStatus() != null)
            entity.setJplStatus(request.getJplStatus());
        if (request.getStationId() != null)
            entity.setStation(StationEntity.builder().id(request.getStationId()).build());
        if (request.getJplLatitude() != null)
            entity.setJplLatitude(request.getJplLatitude());
        if (request.getJplLongitude() != null)
            entity.setJplLongitude(request.getJplLongitude());
        if (request.getHeading() != null)
            entity.setHeading(request.getHeading());
    }
}
