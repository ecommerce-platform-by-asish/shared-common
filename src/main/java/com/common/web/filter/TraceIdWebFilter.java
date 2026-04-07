package com.common.web.filter;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/** Adds traceId to reactive HTTP response headers. */
@Slf4j
@RequiredArgsConstructor
public class TraceIdWebFilter implements WebFilter, Ordered {

  private final Tracer tracer;
  private final ObservationRegistry observationRegistry;

  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }

  @Override
  @NonNull
  public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
    Observation observation =
        Observation.createNotStarted("http.server.requests", observationRegistry)
            .contextualName(
                exchange.getRequest().getMethod() + " " + exchange.getRequest().getURI().getPath())
            .lowCardinalityKeyValue("http.method", exchange.getRequest().getMethod().name())
            .lowCardinalityKeyValue("http.url", exchange.getRequest().getURI().getPath());

    return Mono.defer(
        () -> {
          observation.start();
          try (Observation.Scope _ = observation.openScope()) {
            var currentSpan = tracer.currentSpan();
            if (currentSpan != null && currentSpan.context() != null) {
              String traceId = currentSpan.context().traceId();
              exchange.getResponse().getHeaders().add("X-Trace-Id", traceId);
              // Note: MDC in WebFlux requires additional bridging (e.g. ContextSnapshot)
              // but we are primarily verifying Servlet services for now.
            }
            return chain.filter(exchange).doFinally(signal -> observation.stop());
          }
        });
  }
}
