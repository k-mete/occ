package org.agora.occ.mapper;

import jakarta.enterprise.context.ApplicationScoped;
import org.agora.occ.dto.report.request.ReportRequest;
import org.agora.occ.dto.report.response.ReportResponse;
import org.agora.occ.entity.ReportEntity;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Manual mapper between {@link ReportEntity} and Report DTOs.
 */
@ApplicationScoped
public class ReportMapper {

    /**
     * Converts a {@link ReportEntity} to a {@link ReportResponse}.
     * The comma-separated {@code filePaths} string is split into a
     * {@code List<String>}.
     *
     * @param entity the report entity
     * @return the mapped response DTO
     */
    public ReportResponse toResponse(ReportEntity entity) {
        if (entity == null)
            return null;
        return ReportResponse.builder()
                .id(entity.getId())
                .type(entity.getType())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .filePaths(parseFilePaths(entity.getFilePaths()))
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .trainId(entity.getTrainId())
                .jplId(entity.getJplId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .isRead(entity.getIsRead())
                .build();
    }

    /**
     * Converts a {@link ReportRequest} and list of MinIO object keys into a
     * {@link ReportEntity} ready for persistence.
     *
     * @param request    the multipart form request
     * @param objectKeys the MinIO object URLs/keys of uploaded images
     * @return the mapped entity
     */
    public ReportEntity toEntity(ReportRequest request, List<String> objectKeys) {
        Instant now = Instant.now();
        return ReportEntity.builder()
                .type(request.type != null ? request.type.name() : null)
                .title(request.title)
                .description(request.description)
                .filePaths(String.join(",", objectKeys))
                .latitude(request.latitude)
                .longitude(request.longitude)
                .trainId(request.trainId)
                .jplId(request.jplId)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Splits the comma-separated filePaths string into a list.
     *
     * @param raw the raw comma-separated string from the entity
     * @return a list of individual file paths, or an empty list if null/blank
     */
    private List<String> parseFilePaths(String raw) {
        if (raw == null || raw.isBlank())
            return Collections.emptyList();
        return Arrays.asList(raw.split(","));
    }
}
