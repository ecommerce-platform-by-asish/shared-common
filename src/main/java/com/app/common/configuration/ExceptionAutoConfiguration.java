package com.app.common.configuration;

import com.app.common.exception.handler.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/** Auto-configuration that registers common exception handlers for all services. */
@AutoConfiguration
public class ExceptionAutoConfiguration {

  /** Registers the global exception handler for Servlet-based (MVC) applications. */
  @Configuration(proxyBeanMethods = false)
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
  @Import(GlobalExceptionHandler.Servlet.class)
  static class ServletExceptionConfiguration {}

  /** Registers the global exception handler for Reactive (WebFlux) applications. */
  @Configuration(proxyBeanMethods = false)
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
  @Import(GlobalExceptionHandler.Reactive.class)
  static class ReactiveExceptionConfiguration {}
}
