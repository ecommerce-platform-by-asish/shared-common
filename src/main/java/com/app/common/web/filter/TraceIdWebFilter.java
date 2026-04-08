package com.app.common.web.filter;

import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Lightweight reactive filter that adds the current Micrometer Trace ID to the response headers.
 */
@NullMarked
@RequiredArgsConstructor
public class TraceIdWebFilter implements WebFilter {

  private final Tracer tracer;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    var currentSpan = tracer.currentSpan();
    if (currentSpan != null) {
      exchange.getResponse().getHeaders().add("X-Trace-Id", currentSpan.context().traceId());
    }
    return chain.filter(exchange);
  }
}
