package org.agora.occ.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.agora.occ.dto.occ.request.CreateOccRequest;
import org.agora.occ.dto.occ.response.OccDetailResponse;
import org.agora.occ.dto.occ.response.OccResponse;
import org.agora.occ.dto.station.response.StationResponse;
import org.agora.occ.entity.OccEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class OccMapper {

    /**
     * Converts a request to a new OccEntity.
     */
    public OccEntity toEntity(CreateOccRequest request) {
        return OccEntity.builder()
                .id(UUID.randomUUID())
                .occName(request.getOccName())
                .occLatitude(request.getOccLatitude())
                .occLongitude(request.getOccLongitude())
                .createdAt(Instant.now())
                .build();
    }

    /**
     * Converts an OccEntity to an OccResponse.
     */
    public OccResponse toResponse(OccEntity entity) {
        return OccResponse.builder()
                .id(entity.getId())
                .occName(entity.getOccName())
                .occLatitude(entity.getOccLatitude())
                .occLongitude(entity.getOccLongitude())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Converts an OccEntity and its nested StationResponses to an
     * OccDetailResponse.
     */
    public OccDetailResponse toDetailResponse(OccEntity entity, List<StationResponse> stations) {
        return OccDetailResponse.builder()
                .id(entity.getId())
                .occName(entity.getOccName())
                .occLatitude(entity.getOccLatitude())
                .occLongitude(entity.getOccLongitude())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .stations(stations)
                .build();
    }

    /**
     * Updates an existing OccEntity with data from a request.
     */
    public void updateEntity(OccEntity entity, CreateOccRequest request) {
        if (request.getOccName() != null) {
            entity.setOccName(request.getOccName());
        }
        if (request.getOccLatitude() != null) {
            entity.setOccLatitude(request.getOccLatitude());
        }
        if (request.getOccLongitude() != null) {
            entity.setOccLongitude(request.getOccLongitude());
        }
    }
}
