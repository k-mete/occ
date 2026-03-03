package org.agora.occ.config;

import org.slf4j.MDC;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.util.UUID;

@Provider
@Priority(Priorities.USER)
public class TraceIdFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String TRACE_ID_KEY = "traceId";

    /**
     * Adds trace ID to MDC for each incoming request.
     *
     * @param requestContext the request context
     */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String traceId = requestContext.getHeaderString(TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString();
        }
        MDC.put(TRACE_ID_KEY, traceId);
    }

    /**
     * Adds trace ID header to response and clears MDC.
     *
     * @param requestContext  the request context
     * @param responseContext the response context
     */
    @Override
    public void filter(ContainerRequestContext requestContext,
            ContainerResponseContext responseContext) {
        String traceId = MDC.get(TRACE_ID_KEY);
        if (traceId != null) {
            responseContext.getHeaders().add(TRACE_ID_HEADER, traceId);
        }
        MDC.clear();
    }
}
