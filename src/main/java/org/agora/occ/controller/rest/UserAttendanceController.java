package org.agora.occ.controller.rest;

import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.agora.occ.dto.attendance.response.AttendanceResponse;
import org.agora.occ.dto.common.PagedResult;
import org.agora.occ.service.UserAttendanceService;
import org.agora.occ.util.ApiResponse;
import org.jboss.logging.Logger;

import java.util.UUID;

/**
 * Controller exposing endpoints for querying User Attendance History.
 */
@Path("/api/v1/attendance")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class UserAttendanceController {

    private static final Logger LOG = Logger.getLogger(UserAttendanceController.class);

    private final UserAttendanceService attendanceService;

    @Inject
    public UserAttendanceController(UserAttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /**
     * Retrieves a paginated list of all user attendance logs.
     * Accessible by ADMINISTRASI and PETUGAS_OCC roles.
     *
     * @param page the page number (0-indexed, default 0)
     * @param size the page size (default 10)
     * @return paginated list of attendance logs
     */
    @GET
    @RolesAllowed({ "ADMINISTRASI", "PETUGAS_OCC" })
    public Response getAllAttendance(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        LOG.info("Received request to fetch all attendance logs");
        PagedResult<AttendanceResponse> result = attendanceService.getAllAttendanceHistory(page, size);
        return ApiResponse.paginated(result, "Attendance logs retrieved successfully");
    }

    /**
     * Retrieves a paginated attendance history for a specific user.
     *
     * @param userId the UUID of the target user
     * @param page   the page number (0-indexed, default 0)
     * @param size   the page size (default 10)
     * @return paginated attendance history for the user
     */
    @GET
    @Path("/user/{id}")
    @RolesAllowed({ "ADMINISTRASI", "PETUGAS_OCC" })
    public Response getUserAttendance(
            @PathParam("id") UUID userId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size) {
        LOG.infov("Received request to fetch attendance for user: {0}", userId);
        PagedResult<AttendanceResponse> result = attendanceService.getUserAttendanceHistory(userId, page, size);
        return ApiResponse.paginated(result, "Attendance logs retrieved successfully");
    }
}
