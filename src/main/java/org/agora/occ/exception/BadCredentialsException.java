package org.agora.occ.exception;

import jakarta.ws.rs.core.Response;

/**
 * Exception thrown when a user attempts to log in with an invalid NRP or
 * password.
 * Will be mapped to HTTP 401 Unauthorized.
 */
public class BadCredentialsException extends ApplicationException {

    public BadCredentialsException() {
        super("Invalid NRP or password", Response.Status.UNAUTHORIZED);
    }
}
