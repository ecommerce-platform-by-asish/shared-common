package com.app.common.configuration;

import com.app.common.filter.TraceIdResponseFilter;
import com.app.common.filter.TraceIdWebFilter;
import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;

/** Common tracing and observation configuration. */
@Slf4j
@Configuration
@PropertySource("classpath:common-tracing.properties")
@AutoConfigureAfter(
    name =
        "org.springframework.boot.actuate.autoconfigure.tracing.MicrometerTracingAutoConfiguration")
public class TracingAutoConfiguration {

  public TracingAutoConfiguration() {
    log.info("Initializing TracingAutoConfiguration...");
  }

  /** Tracing configuration for Servlet-based applications. */
  @Configuration(proxyBeanMethods = false)
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
  @ConditionalOnClass(name = "org.springframework.boot.web.servlet.FilterRegistrationBean")
  @ConditionalOnBean(Tracer.class)
  static class ServletTracingConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "traceIdResponseFilter")
    public FilterRegistrationBean<TraceIdResponseFilter> traceIdResponseFilter(Tracer tracer) {
      var registrationBean = new FilterRegistrationBean<>(new TraceIdResponseFilter(tracer));
      registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);
      return registrationBean;
    }
  }

  /** Tracing configuration for Reactive (WebFlux) applications. */
  @Configuration(proxyBeanMethods = false)
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
  @ConditionalOnClass(name = "org.springframework.web.server.WebFilter")
  @ConditionalOnBean(Tracer.class)
  static class ReactiveTracingConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "traceIdWebFilter")
    public TraceIdWebFilter traceIdWebFilter(Tracer tracer) {
      return new TraceIdWebFilter(tracer);
    }
  }
}
