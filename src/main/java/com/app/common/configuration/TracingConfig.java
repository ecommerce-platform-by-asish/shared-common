package com.app.common.configuration;

import org.jspecify.annotations.NullMarked;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;

/**
 * Shared Tracing Configuration for the eCommerce ecosystem.
 *
 * <p>This configuration ensures that Micrometer Tracing context is automatically propagated through
 * Reactor's asynchronous and reactive streams.
 */
@Configuration
@NullMarked
public class TracingConfig {

  /**
   * Enables automatic context propagation for Project Reactor. This is essential for maintaining
   * traceId and userId in reactive environments like Spring Cloud Gateway.
   */
  @Bean
  public ApplicationListener<ApplicationStartedEvent> tracingBootstrap() {
    return _ -> Hooks.enableAutomaticContextPropagation();
  }
}
