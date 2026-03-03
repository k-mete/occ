package org.agora.occ.dto.common;

import lombok.Data;

import java.time.Instant;

/**
 * Standard API response object.
 */
@Data
public class StandardResponse {

    private boolean status;
    private String message;
    private int code;
    private Object data;
    private Instant timestamp;
    private String traceId;
}
