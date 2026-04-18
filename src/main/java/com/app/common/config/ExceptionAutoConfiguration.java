package com.app.common.config;

import com.app.common.exception.GlobalExceptionHandler;
import com.app.common.exception.ReactiveGlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/** Auto-configuration for global exception handlers. */
@AutoConfiguration
public class ExceptionAutoConfiguration {

  /** Exception configuration for Servlet-based applications. */
  @Configuration(proxyBeanMethods = false)
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
  @Import(GlobalExceptionHandler.class)
  static class ServletExceptionConfiguration {}

  /** Exception configuration for Reactive (WebFlux) applications. */
  @Configuration(proxyBeanMethods = false)
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
  @Import(ReactiveGlobalExceptionHandler.class)
  static class ReactiveExceptionConfiguration {}
}
