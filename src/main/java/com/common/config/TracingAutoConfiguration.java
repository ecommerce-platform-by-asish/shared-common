package com.common.config;

import com.common.web.filter.TraceIdResponseFilter;
import com.common.web.filter.TraceIdWebFilter;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.otel.bridge.OtelCurrentTraceContext;
import io.micrometer.tracing.otel.bridge.OtelTracer;
import io.micrometer.tracing.otel.bridge.Slf4JEventListener;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/** Common tracing and observation configuration. */
@Slf4j
@Configuration
@PropertySource("classpath:common-tracing.properties")
public class TracingAutoConfiguration {

  public TracingAutoConfiguration() {
    log.info("Initializing TracingAutoConfiguration...");
  }

  /** Registers servlet filter for traceId response headers. */
  @Bean
  @ConditionalOnMissingBean(name = "traceIdResponseFilter")
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
  @ConditionalOnClass(name = "jakarta.servlet.Filter")
  public FilterRegistrationBean<TraceIdResponseFilter> traceIdResponseFilter(Tracer tracer) {
    var registrationBean = new FilterRegistrationBean<>(new TraceIdResponseFilter(tracer));
    registrationBean.setOrder(org.springframework.core.Ordered.HIGHEST_PRECEDENCE);
    return registrationBean;
  }

  /** Registers reactive filter for traceId response headers. */
  @Bean
  @ConditionalOnMissingBean(name = "traceIdWebFilter")
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
  @ConditionalOnClass(name = "org.springframework.web.server.WebFilter")
  public TraceIdWebFilter traceIdWebFilter(Tracer tracer) {
    return new TraceIdWebFilter(tracer);
  }

  @Bean
  @ConditionalOnMissingBean
  public OpenTelemetry openTelemetry(SpanExporter spanExporter) {
    SdkTracerProvider tracerProvider =
        SdkTracerProvider.builder()
            .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
            .build();

    return OpenTelemetrySdk.builder()
        .setTracerProvider(tracerProvider)
        .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
        .build();
  }

  @Bean
  @ConditionalOnMissingBean
  public Tracer tracer(OpenTelemetry openTelemetry) {
    io.opentelemetry.api.trace.Tracer otelTracer = openTelemetry.getTracer("com.common");
    OtelCurrentTraceContext otelCurrentTraceContext = new OtelCurrentTraceContext();
    Slf4JEventListener slf4JEventListener = new Slf4JEventListener();
    return new OtelTracer(otelTracer, otelCurrentTraceContext, slf4JEventListener::onEvent);
  }

  @Bean
  @ConditionalOnMissingBean
  public SpanExporter loggingSpanExporter() {
    return LoggingSpanExporter.create();
  }
}
