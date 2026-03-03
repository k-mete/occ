package org.agora.occ.dto.common;

import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * Standard paginated API response object.
 *
 * @param <T> the type of data in the response
 */
@Data
public class PagedResponse<T> {

    private boolean status;
    private String message;
    private int code;
    private List<T> data;
    private Instant timestamp;
    private String traceId;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
