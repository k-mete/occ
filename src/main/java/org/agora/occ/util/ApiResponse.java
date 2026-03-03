package org.agora.occ.util;

import org.agora.occ.dto.common.PagedResponse;
import org.agora.occ.dto.common.PagedResult;
import org.agora.occ.dto.common.StandardResponse;
import org.slf4j.MDC;

import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.util.UUID;

/**
 * Utility class for creating standardized API responses.
 */
public class ApiResponse {

    private ApiResponse() {
        // Utility class
    }

    /**
     * Creates a successful response with HTTP 200.
     *
     * @param data    the response data
     * @param message the success message
     * @return JAX-RS Response object
     */
    public static Response ok(Object data, String message) {
        return buildResponse(Response.Status.OK, data, message);
    }

    /**
     * Creates a successful response with HTTP 201.
     *
     * @param data    the created resource data
     * @param message the success message
     * @return JAX-RS Response object
     */
    public static Response created(Object data, String message) {
        return buildResponse(Response.Status.CREATED, data, message);
    }

    /**
     * Creates a paginated response with HTTP 200.
     *
     * @param pagedResult the paginated result
     * @param message     the success message
     * @param <T>         the type of data
     * @return JAX-RS Response object
     */
    public static <T> Response paginated(PagedResult<T> pagedResult, String message) {
        PagedResponse<T> response = new PagedResponse<>();
        response.setStatus(true);
        response.setMessage(message);
        response.setCode(Response.Status.OK.getStatusCode());
        response.setData(pagedResult.getData());
        response.setTimestamp(Instant.now());
        response.setTraceId(getTraceId());
        response.setPage(pagedResult.getPage());
        response.setSize(pagedResult.getSize());
        response.setTotalElements(pagedResult.getTotalElements());
        response.setTotalPages(pagedResult.getTotalPages());
        return Response.ok(response).build();
    }

    /**
     * Creates an error response.
     *
     * @param status  the HTTP status
     * @param message the error message
     * @return JAX-RS Response object
     */
    public static Response error(Response.Status status, String message) {
        return buildResponse(status, null, message, false);
    }

    private static Response buildResponse(Response.Status status, Object data, String message) {
        return buildResponse(status, data, message, true);
    }

    private static Response buildResponse(Response.Status status, Object data,
            String message, boolean success) {
        StandardResponse response = new StandardResponse();
        response.setStatus(success);
        response.setMessage(message);
        response.setCode(status.getStatusCode());
        response.setData(data);
        response.setTimestamp(Instant.now());
        response.setTraceId(getTraceId());
        return Response.status(status).entity(response).build();
    }

    private static String getTraceId() {
        String traceId = MDC.get("traceId");
        return traceId != null ? traceId : UUID.randomUUID().toString();
    }
}
