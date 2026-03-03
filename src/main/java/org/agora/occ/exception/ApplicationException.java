package org.agora.occ.exception;

import jakarta.ws.rs.core.Response;

/**
 * Base exception for application specific business errors.
 */
public class ApplicationException extends RuntimeException {

    private final Response.Status status;

    public ApplicationException(String message) {
        super(message);
        this.status = Response.Status.BAD_REQUEST;
    }

    public ApplicationException(String message, Response.Status status) {
        super(message);
        this.status = status;
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
        this.status = Response.Status.INTERNAL_SERVER_ERROR;
    }

    public Response.Status getStatus() {
        return status;
    }
}
