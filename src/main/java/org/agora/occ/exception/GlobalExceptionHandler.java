package org.agora.occ.exception;

import org.agora.occ.dto.common.StandardResponse;
import org.agora.occ.dto.common.ValidationErrorResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 */
public class GlobalExceptionHandler {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles invalid login attempts.
     */
    @ServerExceptionMapper
    public Response handleBadCredentialsException(BadCredentialsException ex) {
        LOG.warnv("Authentication failed: {0}", ex.getMessage());
        return buildErrorResponse(ex.getStatus(), ex.getMessage());
    }

    /**
     * Handles custom application exceptions.
     *
     * @param ex the application exception
     * @return standardized error response
     */
    @ServerExceptionMapper
    public Response handleApplicationException(ApplicationException ex) {
        LOG.warnv("Application exception: {0}", ex.getMessage());
        return buildErrorResponse(ex.getStatus(), ex.getMessage());
    }

    /**
     * Handles resource not found exceptions.
     *
     * @param ex the not found exception
     * @return standardized error response with 404 status
     */
    @ServerExceptionMapper
    public Response handleNotFoundException(NotFoundException ex) {
        LOG.warnv("Resource not found: {0}", ex.getMessage());
        return buildErrorResponse(Response.Status.NOT_FOUND, "Resource not found");
    }

    /**
     * Handles validation exceptions.
     *
     * @param ex the constraint violation exception
     * @return standardized error response with validation details
     */
    @ServerExceptionMapper
    public Response handleValidationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.toList());

        LOG.warnv("Validation failed: {0}", errors);

        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        errorResponse.setErrors(errors);

        StandardResponse response = new StandardResponse();
        response.setStatus(false);
        response.setMessage("Validation failed");
        response.setCode(Response.Status.BAD_REQUEST.getStatusCode());
        response.setData(errorResponse);
        response.setTimestamp(Instant.now());
        response.setTraceId(getTraceId());

        return Response.status(Response.Status.BAD_REQUEST).entity(response).build();
    }

    /**
     * Handles all unhandled exceptions.
     *
     * @param ex the exception
     * @return standardized error response with 500 status
     */
    @ServerExceptionMapper
    public Response handleGenericException(Exception ex) {
        LOG.errorv(ex, "Unexpected error: {0}", ex.getMessage());
        return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred");
    }

    private Response buildErrorResponse(Response.Status status, String message) {
        StandardResponse response = new StandardResponse();
        response.setStatus(false);
        response.setMessage(message);
        response.setCode(status.getStatusCode());
        response.setData(null);
        response.setTimestamp(Instant.now());
        response.setTraceId(getTraceId());

        return Response.status(status).entity(response).build();
    }

    private String getTraceId() {
        String traceId = MDC.get("traceId");
        return traceId != null ? traceId : UUID.randomUUID().toString();
    }
}
