package com.app.common.exception.handler;

import com.app.common.dto.ApiResponse;
import com.app.common.exception.BaseException;
import com.app.common.exception.GlobalStatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

/** Central exception handler for Reactive (WebFlux) applications. */
@Slf4j
@Component
@Order(-2)
@RequiredArgsConstructor
public class ReactiveGlobalExceptionHandler implements ErrorWebExceptionHandler {

  private final ObjectMapper objectMapper;

  @Override
  public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
    log.error("Reactive error occurred: {}", ex.getMessage());

    var apiResponse =
        switch (ex) {
          case BaseException baseEx -> ApiResponse.error(baseEx.getErrorCode());
          default -> ApiResponse.error(GlobalStatusCode.INTERNAL_SERVER_ERROR);
        };

    HttpStatus status =
        switch (ex) {
          case BaseException baseEx -> baseEx.getErrorCode().getHttpStatus();
          default -> GlobalStatusCode.INTERNAL_SERVER_ERROR.getHttpStatus();
        };

    exchange.getResponse().setStatusCode(status);
    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

    return exchange
        .getResponse()
        .writeWith(
            Mono.fromCallable(() -> objectMapper.writeValueAsBytes(apiResponse))
                .map(exchange.getResponse().bufferFactory()::wrap));
  }
}
