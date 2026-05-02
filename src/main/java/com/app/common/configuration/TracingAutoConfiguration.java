package com.app.common.configuration;

import com.app.common.filter.TraceIdResponseFilter;
import com.app.common.filter.TraceIdWebFilter;
import io.micrometer.tracing.Tracer;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Hooks;

/**
 * Main Tracing Auto-Configuration. Leverages native Spring Boot 4.1.0-RC1 tracing while providing
 * essential Reactor context propagation and custom trace filters.
 */
@Slf4j
@AutoConfiguration
@PropertySource("classpath:common-tracing.properties")
public class TracingAutoConfiguration {

  @Value("${spring.application.name:unknown-service}")
  private String serviceName;

  public TracingAutoConfiguration() {}

  @jakarta.annotation.PostConstruct
  public void init() {
    log.info("Initializing TracingAutoConfiguration for service: {}", serviceName);
  }

  /**
   * Enables automatic context propagation for Reactor if it is on the classpath. This is essential
   * for Micrometer Tracing to work correctly in reactive flows.
   */
  @Slf4j
  @Configuration(proxyBeanMethods = false)
  @ConditionalOnClass(Hooks.class)
  static class ReactorTracingConfiguration {

    @Bean
    public ApplicationListener<ApplicationStartedEvent> tracingBootstrap() {
      return _ -> {
        log.info("Enabling Reactor automatic context propagation");
        Hooks.enableAutomaticContextPropagation();
      };
    }
  }

  /** Tracing configuration for Servlet-based applications. */
  @Configuration(proxyBeanMethods = false)
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
  @ConditionalOnClass(FilterRegistrationBean.class)
  static class ServletTracingConfiguration {
    @Bean
    public FilterRegistrationBean<TraceIdResponseFilter> traceIdResponseFilter(
        ObjectProvider<Tracer> tracerProvider) {
      var registrationBean =
          new FilterRegistrationBean<>(new TraceIdResponseFilter(tracerProvider.getIfAvailable()));
      registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);
      return registrationBean;
    }
  }

  /** Tracing configuration for Reactive (WebFlux) applications. */
  @Configuration(proxyBeanMethods = false)
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
  @ConditionalOnClass(WebFilter.class)
  static class ReactiveTracingConfiguration {
    @Bean
    public TraceIdWebFilter traceIdWebFilter(ObjectProvider<Tracer> tracerProvider) {
      return new TraceIdWebFilter(Objects.requireNonNull(tracerProvider.getIfAvailable()));
    }
  }
}
