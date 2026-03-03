package org.agora.occ.controller.rest;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.agora.occ.dto.auth.request.LoginRequest;
import org.agora.occ.dto.auth.request.RefreshTokenRequest;
import org.agora.occ.dto.auth.response.LoginResponse;
import org.agora.occ.dto.auth.response.ProfileResponse;
import org.agora.occ.dto.auth.response.TokenRefreshResponse;
import org.agora.occ.service.AuthService;
import org.agora.occ.util.ApiResponse;
import org.jboss.logging.Logger;

/**
 * Controller exposing endpoints for JWT authentication.
 */
@Path("/api/v1/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class AuthController {

    private static final Logger LOG = Logger.getLogger(AuthController.class);

    private final AuthService authService;

    @Inject
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Authenticates a user with NRP and password.
     * 
     * @param request the login credentials
     * @return the access token, refresh token, user details, and check-in status
     */
    @POST
    @Path("/login")
    @PermitAll
    public Response login(@Valid LoginRequest request) {
        LOG.info("Received login request for NRP: " + request.getNrp());
        LoginResponse response = authService.login(request);
        return ApiResponse.ok(response, "Login successful");
    }

    /**
     * Refreshes an expired access token using a valid refresh token.
     *
     * @param request the refresh token
     * @return a new access token
     */
    @POST
    @Path("/token/refresh")
    @PermitAll
    public Response refreshToken(@Valid RefreshTokenRequest request) {
        LOG.info("Received token refresh request");
        TokenRefreshResponse response = authService.refresh(request);
        return ApiResponse.ok(response, "Token refreshed successfully");
    }

    /**
     * Logs out the user by revoking their refresh token.
     *
     * @param request the refresh token to revoke
     * @return success message
     */
    @POST
    @Path("/logout")
    @RolesAllowed({ "PETUGAS_OCC", "PETUGAS_JPL", "MASINIS", "ADMINISTRASI" })
    public Response logout(@Valid RefreshTokenRequest request) {
        LOG.info("Received logout request");
        authService.logout(request);
        return ApiResponse.ok(null, "Logged out successfully");
    }

    /**
     * Retrieves the profile of the currently authenticated user.
     *
     * @return the user profile and session data
     */
    @GET
    @Path("/profile")
    @RolesAllowed({ "PETUGAS_OCC", "PETUGAS_JPL", "MASINIS", "ADMINISTRASI" })
    public Response getProfile() {
        LOG.info("Received profile fetch request");
        ProfileResponse response = authService.getProfile();
        return ApiResponse.ok(response, "Profile retrieved successfully");
    }
}
