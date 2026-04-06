package com.common.web.filter;

import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/** Adds traceId to reactive HTTP response headers. */
@Slf4j
@RequiredArgsConstructor
public class TraceIdWebFilter implements WebFilter, Ordered {

  private final Tracer tracer;

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }

  @Override
  @NonNull
  public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
    return chain
        .filter(exchange)
        .contextWrite(
            context -> {
              // Extract traceId from Micrometer Tracing
              var currentSpan = tracer.currentSpan();
              if (currentSpan != null && currentSpan.context() != null) {
                String traceId = currentSpan.context().traceId();
                exchange.getResponse().getHeaders().add("X-Trace-Id", traceId);
              }
              return context;
            });
  }
}
