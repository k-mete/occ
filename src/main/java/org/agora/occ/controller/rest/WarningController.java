package org.agora.occ.controller.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.agora.occ.dto.common.PagedResult;
import org.agora.occ.dto.warning.WarningResponse;
import org.agora.occ.enums.WarningLevel;
import org.agora.occ.service.WarningService;
import org.agora.occ.util.ApiResponse;
import org.jboss.logging.Logger;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Controller for accessing historical warning data.
 */
@Path("/api/v1/warnings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class WarningController {

    private static final Logger LOG = Logger.getLogger(WarningController.class);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final WarningService warningService;

    @Inject
    public WarningController(WarningService warningService) {
        this.warningService = warningService;
    }

    /**
     * Retrieves historical warnings with optional filtering and pagination.
     * If {@code size} is -1, pagination is skipped and all results are returned.
     *
     * @param jplId        optional JPL ID
     * @param trainId      optional Train ID
     * @param warningLevel optional severity level
     * @param dateFromRaw  optional start date (dd/MM/yyyy)
     * @param dateToRaw    optional end date (dd/MM/yyyy)
     * @param page         the page number (default: 0)
     * @param size         the page size (default: 10). -1 for unpaginated.
     * @return paginated or unpaginated list of warnings
     */
    @GET
    public Response getWarnings(
            @QueryParam("jplId") UUID jplId,
            @QueryParam("trainId") UUID trainId,
            @QueryParam("warningLevel") WarningLevel warningLevel,
            @QueryParam("dateFrom") String dateFromRaw,
            @QueryParam("dateTo") String dateToRaw,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {

        LOG.infov("Received request to fetch warnings with jplId={0}, trainId={1}, level={2}, dateFrom={3}, dateTo={4}",
                jplId, trainId, warningLevel, dateFromRaw, dateToRaw);

        Instant dateFrom = parseDate(dateFromRaw, false);
        Instant dateTo = parseDate(dateToRaw, true);

        if (size == -1) {
            List<WarningResponse> results = warningService.getWarningsUnpaginated(
                    jplId, trainId, warningLevel, dateFrom, dateTo);
            return ApiResponse.ok(results, "Warnings retrieved successfully");
        } else {
            PagedResult<WarningResponse> pagedResult = warningService.getWarningsPaginated(
                    jplId, trainId, warningLevel, dateFrom, dateTo, page, size);
            return ApiResponse.paginated(pagedResult, "Warnings retrieved successfully");
        }
    }

    /**
     * Parses a date string in dd/MM/yyyy format to an Instant in UTC.
     * Returns null if the input is null or empty.
     *
     * @param raw      the raw date string
     * @param endOfDay whether to map the time to 23:59:59 or 00:00:00
     * @return the resolved Instant, or null
     */
    private Instant parseDate(String raw, boolean endOfDay) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(raw.trim(), DATE_FMT);
            if (endOfDay) {
                return date.atTime(23, 59, 59).toInstant(ZoneOffset.UTC);
            } else {
                return date.atStartOfDay().toInstant(ZoneOffset.UTC);
            }
        } catch (Exception e) {
            LOG.warnv("Failed to parse date string {0}: {1}", raw, e.getMessage());
            return null; // Return null to ignore invalid filters rather than crashing
        }
    }
}
