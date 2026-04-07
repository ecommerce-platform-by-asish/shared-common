package com.common.exception;

import com.common.web.dto.ApiResponse;
import jakarta.annotation.PostConstruct;
import java.net.ConnectException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.webflux.autoconfigure.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.webflux.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * Handles WebFlux exceptions, overriding Spring's default HTML/JSON error dumps. This is especially
 * useful for Spring Cloud Gateway, which uses WebFlux and typically throws
 * java.net.ConnectException if a route target is down.
 */
@Slf4j
@Order(-2) // Before DefaultErrorWebExceptionHandler
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveGlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

  private final ServerCodecConfigurer serverCodecConfigurer;

  public ReactiveGlobalExceptionHandler(
      ErrorAttributes errorAttributes,
      WebProperties webProperties,
      ApplicationContext applicationContext,
      ServerCodecConfigurer serverCodecConfigurer) {
    super(errorAttributes, webProperties.getResources(), applicationContext);
    this.serverCodecConfigurer = serverCodecConfigurer;
  }

  @PostConstruct
  public void init() {
    this.setMessageWriters(serverCodecConfigurer.getWriters());
    this.setMessageReaders(serverCodecConfigurer.getReaders());
  }

  @Override
  protected RouterFunction<ServerResponse> getRoutingFunction(
      @NonNull ErrorAttributes errorAttributes) {
    return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
  }

  private Mono<ServerResponse> renderErrorResponse(@NonNull ServerRequest request) {
    Throwable error = getError(request);

    if (error == null) {
      return ServerResponse.status(HttpStatus.OK)
          .contentType(MediaType.APPLICATION_JSON)
          .body(BodyInserters.fromValue(ApiResponse.error(GlobalStatusCode.INTERNAL_SERVER_ERROR)));
    }

    log.error(
        "Unhandled WebFlux {} at path: {}", error.getClass().getName(), request.path(), error);

    GlobalStatusCode statusCode = GlobalStatusCode.INTERNAL_SERVER_ERROR;

    if (error instanceof ConnectException
        || (error.getCause() != null && error.getCause() instanceof ConnectException)
        || error.getClass().getName().contains("AnnotatedConnectException")
        || (error.getCause() != null
            && error.getCause().getClass().getName().contains("AnnotatedConnectException"))) {
      statusCode = GlobalStatusCode.SERVICE_UNAVAILABLE;
    }

    ApiResponse<Void> apiResponse = ApiResponse.error(statusCode);

    return ServerResponse.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(apiResponse));
  }
}
