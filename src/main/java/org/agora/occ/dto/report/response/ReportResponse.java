package org.agora.occ.dto.report.response;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for a report, returned after creation or on retrieval.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class ReportResponse {

    private UUID id;

    /** LOCO or JPL. */
    private String type;

    private String title;
    private String description;

    /** MinIO public URLs for the uploaded images. */
    private List<String> filePaths;

    private Double latitude;
    private Double longitude;

    private UUID trainId;
    private UUID jplId;

    private Instant createdAt;
    private Instant updatedAt;
    private Boolean isRead;
}
