package com.app.common.web.filter;

import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

/** Lightweight filter that adds the current Micrometer Trace ID to the response headers. */
@RequiredArgsConstructor
public class TraceIdResponseFilter extends OncePerRequestFilter {

  private final Tracer tracer;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    var currentSpan = tracer.currentSpan();
    if (currentSpan != null) {
      response.setHeader("X-Trace-Id", currentSpan.context().traceId());
    }
    filterChain.doFilter(request, response);
  }
}
