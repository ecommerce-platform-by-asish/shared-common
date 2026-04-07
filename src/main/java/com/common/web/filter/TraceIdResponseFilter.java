package com.common.web.filter;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

/** Adds traceId to HTTP response headers. */
@RequiredArgsConstructor
public class TraceIdResponseFilter extends OncePerRequestFilter {

  private final Tracer tracer;
  private final ObservationRegistry observationRegistry;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    // Manually start an observation if one is not present to trigger tracing spans
    Observation observation =
        Observation.createNotStarted("http.server.requests", observationRegistry)
            .contextualName(request.getMethod() + " " + request.getRequestURI())
            .lowCardinalityKeyValue("http.method", request.getMethod())
            .lowCardinalityKeyValue("http.url", request.getRequestURI())
            .start();

    try (Observation.Scope _ = observation.openScope()) {
      var currentSpan = tracer.currentSpan();
      if (currentSpan != null) {
        String traceId = currentSpan.context().traceId();
        String spanId = currentSpan.context().spanId();

        // Brute force MDC population
        MDC.put("traceId", traceId);
        MDC.put("spanId", spanId);

        response.setHeader("X-Trace-Id", traceId);
      }
      filterChain.doFilter(request, response);
    } catch (Exception e) {
      observation.error(e);
      throw e;
    } finally {
      observation.stop();
      MDC.remove("traceId");
      MDC.remove("spanId");
    }
  }
}
