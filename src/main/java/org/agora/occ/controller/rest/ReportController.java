package org.agora.occ.controller.rest;

import jakarta.inject.Inject;
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
import org.agora.occ.dto.report.request.ReportRequest;
import org.agora.occ.dto.report.response.ReportResponse;
import org.agora.occ.enums.ReportFileType;
import org.agora.occ.service.ReportService;
import org.agora.occ.util.ApiResponse;
import org.jboss.logging.Logger;

import java.io.InputStream;
import java.util.UUID;

/**
 * REST resource for field reports: multipart upload, retrieval, and binary
 * download.
 */
@Path("/api/v1/reports")
@Produces(MediaType.APPLICATION_JSON)
public class ReportController {

    private static final Logger LOG = Logger.getLogger(ReportController.class);

    private final ReportService reportService;

    @Inject
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Creates a new report by uploading images to MinIO.
     *
     * @param request the multipart form data
     * @return 201 with the created report, or 500 on error
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createReport(ReportRequest request) {
        LOG.info("Received request to create report");
        try {
            ReportResponse report = reportService.createReport(request);
            return ApiResponse.created(report, "Report created successfully");
        } catch (Exception e) {
            LOG.errorv("Error creating report: {0}", e.getMessage());
            return ApiResponse.error(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error creating report: " + e.getMessage());
        }
    }

    /**
     * Retrieves all reports with optional filters and pagination.
     *
     * @param page      0-indexed page number
     * @param size      page size (use -1 for unpaginated)
     * @param type      optional type filter
     * @param jplName   optional JPL name substring
     * @param trainName optional train name substring
     * @param date      optional date string filter
     * @param jplId     optional JPL ID filter
     * @param trainId   optional train ID filter
     * @return paginated list of report responses
     */
    @GET
    public Response getAllReports(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size,
            @QueryParam("type") ReportFileType type,
            @QueryParam("jplName") String jplName,
            @QueryParam("trainName") String trainName,
            @QueryParam("date") String date,
            @QueryParam("jplId") UUID jplId,
            @QueryParam("trainId") UUID trainId) {
        LOG.infov("Fetching reports. Page: {0}, Size: {1}, Type: {2}", page, size, type);
        PagedResult<ReportResponse> result = reportService.getAllReports(
                page, size, type != null ? type.name() : null,
                jplName, trainName, date, jplId, trainId);
        return ApiResponse.paginated(result, "Reports retrieved successfully");
    }

    /**
     * Retrieves a single report by its unique identifier.
     *
     * @param id the report UUID
     * @return 200 with the report, or 404 if not found
     */
    @GET
    @Path("/{id}")
    public Response getReportById(@PathParam("id") UUID id) {
        LOG.infov("Fetching report with id: {0}", id);
        ReportResponse report = reportService.getReportById(id);
        if (report == null) {
            return ApiResponse.error(Response.Status.NOT_FOUND, "Report not found");
        }
        return ApiResponse.ok(report, "Report retrieved successfully");
    }

    /**
     * Downloads a report file from MinIO.
     * Example: {@code GET /api/v1/reports/download?fileName=loco/uuid/image.jpg}
     *
     * @param fileName the MinIO object key (relative path or full URL)
     * @return the binary file content as an octet stream
     */
    @GET
    @Path("/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@QueryParam("fileName") String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("fileName is required").build();
        }
        try {
            InputStream stream = reportService.getReportFile(fileName);
            String downloadName = fileName.contains("/")
                    ? fileName.substring(fileName.lastIndexOf('/') + 1)
                    : fileName;
            return Response.ok(stream)
                    .header("Content-Disposition", "attachment; filename=\"" + downloadName + "\"")
                    .build();
        } catch (Exception e) {
            LOG.errorv("Error downloading file: {0} — {1}", fileName, e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity("File not found").build();
        }
    }
}
