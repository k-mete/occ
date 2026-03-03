package org.agora.occ.dto.report.request;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.ws.rs.core.MediaType;
import org.agora.occ.enums.ReportFileType;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.List;
import java.util.UUID;

/**
 * Multipart form-data request for creating a report.
 * All fields are bound via {@code @RestForm} for RESTEasy Reactive multipart
 * support.
 */
@RegisterForReflection
public class ReportRequest {

    /** Report category: LOCO or JPL. */
    @RestForm
    public ReportFileType type;

    @RestForm
    public String title;

    @RestForm
    public String description;

    /** One or more binary image files to upload to MinIO. */
    @RestForm("images")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    public List<FileUpload> images;

    @RestForm
    public Double latitude;

    @RestForm
    public Double longitude;

    /** ID of the train that created the report (used when type = LOCO). */
    @RestForm
    public UUID trainId;

    /** ID of the JPL that created the report (used when type = JPL). */
    @RestForm
    public UUID jplId;
}
