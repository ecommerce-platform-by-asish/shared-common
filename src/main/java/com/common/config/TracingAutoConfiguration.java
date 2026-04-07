package com.common.config;

import com.common.web.filter.TraceIdResponseFilter;
import com.common.web.filter.TraceIdWebFilter;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.handler.DefaultTracingObservationHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
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
@AutoConfigureAfter(
    name =
        "org.springframework.boot.actuate.autoconfigure.tracing.MicrometerTracingAutoConfiguration")
public class TracingAutoConfiguration {

  @Bean
  public ObservationHandler<Observation.Context> tracingObservationHandler(Tracer tracer) {
    return new DefaultTracingObservationHandler(tracer);
  }

  public TracingAutoConfiguration() {
    log.info("Initializing TracingAutoConfiguration...");
  }

  @Bean
  @ConditionalOnMissingBean(name = "traceIdResponseFilter")
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
  @ConditionalOnClass(name = "jakarta.servlet.Filter")
  public FilterRegistrationBean<TraceIdResponseFilter> traceIdResponseFilter(
      ObjectProvider<Tracer> tracerProvider, ObservationRegistry observationRegistry) {
    Tracer tracer = tracerProvider.getIfAvailable();
    if (tracer == null) {
      log.warn("Tracer bean NOT found for TraceIdResponseFilter. Tracing will be disabled.");
      return null;
    }
    var registrationBean =
        new FilterRegistrationBean<>(new TraceIdResponseFilter(tracer, observationRegistry));
    registrationBean.setOrder(org.springframework.core.Ordered.HIGHEST_PRECEDENCE);
    return registrationBean;
  }

  /** Registers reactive filter for traceId response headers. */
  @Bean
  @ConditionalOnMissingBean(name = "traceIdWebFilter")
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
  @ConditionalOnClass(name = "org.springframework.web.server.WebFilter")
  public TraceIdWebFilter traceIdWebFilter(
      ObjectProvider<Tracer> tracerProvider, ObservationRegistry observationRegistry) {
    Tracer tracer = tracerProvider.getIfAvailable();
    if (tracer == null) {
      log.warn("Tracer bean NOT found for TraceIdWebFilter. Tracing will be disabled.");
      return null;
    }
    return new TraceIdWebFilter(tracer, observationRegistry);
  }
}
