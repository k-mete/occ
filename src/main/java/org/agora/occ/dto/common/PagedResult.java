package org.agora.occ.dto.common;

import lombok.Getter;

import java.util.List;

/**
 * Result object for pagination queries before being wrapped in the response.
 *
 * @param <T> the type of data representing an item
 */
@Getter
public class PagedResult<T> {

    private final List<T> data;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    /**
     * Creates a paged result from query results.
     *
     * @param data          the list of items
     * @param page          the current page number
     * @param size          the page size
     * @param totalElements the total number of elements
     */
    public PagedResult(List<T> data, int page, int size, long totalElements) {
        this.data = data;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
    }
}
