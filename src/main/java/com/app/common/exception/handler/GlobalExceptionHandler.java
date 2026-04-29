package com.app.common.exception.handler;

import com.app.common.dto.ApiResponse;
import com.app.common.exception.BaseException;
import com.app.common.exception.GlobalStatusCode;
import com.app.common.exception.StatusCode;
import com.app.common.exception.ValidationError;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

/** Holder for User Identity filters that propagate Gateway headers into Security Contexts. */
public final class GlobalExceptionHandler {

  private GlobalExceptionHandler() {}

  /** Intercepts system exceptions to return standardized API responses in Servlet apps. */
  @Slf4j
  @RestControllerAdvice
  @org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication(
      type =
          org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type.SERVLET)
  public static class Servlet {

    /** Handles domain-specific business exceptions. */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException ex) {
      log.error("Business exception: {}", ex.getMessage());
      StatusCode statusCode = ex.getErrorCode();
      ApiResponse<Void> response = ApiResponse.error(statusCode);
      return new ResponseEntity<>(response, statusCode.getHttpStatus());
    }

    /** Maps bean validation failures to detailed error reports. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
        MethodArgumentNotValidException ex) {
      List<ValidationError> errors =
          ex.getBindingResult().getFieldErrors().stream()
              .map(e -> new ValidationError(e.getField(), e.getDefaultMessage()))
              .toList();

      ApiResponse<Void> response =
          ApiResponse.error(
              GlobalStatusCode.VALIDATION_ERROR.getMessage(),
              GlobalStatusCode.VALIDATION_ERROR.toString(),
              errors);

      return ResponseEntity.badRequest().body(response);
    }

    /** Handles database access and connectivity issues. */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataAccessException(DataAccessException ex) {
      log.warn("Data access error: {}", ex.getMessage());
      ApiResponse<Void> response =
          ApiResponse.error(
              GlobalStatusCode.BAD_REQUEST,
              "Invalid request parameters: " + ex.getMostSpecificCause().getMessage());
      return ResponseEntity.badRequest().body(response);
    }

    /** Fallback handler for all uncaught system exceptions. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
      log.error("Unhandled exception occurred", ex);
      ApiResponse<Void> response = ApiResponse.error(GlobalStatusCode.INTERNAL_SERVER_ERROR);
      return new ResponseEntity<>(response, GlobalStatusCode.INTERNAL_SERVER_ERROR.getHttpStatus());
    }
  }

  /**
   * Intercepts system failures in Reactive applications to return standardized ProblemDetail
   * responses (RFC 7807).
   */
  @Slf4j
  @Component
  @Order(-2)
  @RequiredArgsConstructor
  @org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication(
      type =
          org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type
              .REACTIVE)
  public static class Reactive implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;
    private final ObservationRegistry observationRegistry;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
      ProblemDetail problem =
          switch (ex) {
            case BaseException be -> {
              ProblemDetail p =
                  ProblemDetail.forStatusAndDetail(
                      be.getErrorCode().getHttpStatus(), be.getMessage());
              p.setTitle(be.getErrorCode().toString());
              yield p;
            }
            case org.springframework.web.server.ResponseStatusException rse ->
                ProblemDetail.forStatusAndDetail(rse.getStatusCode(), rse.getReason());
            default ->
                ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
          };

      // Modern Micrometer pattern: wrap logging in observation scope
      Observation observation =
          exchange.getAttribute(
              "org.springframework.cloud.gateway.support.ServerWebExchangeUtils.gatewayObservation");
      if (observation == null) {
        observation = observationRegistry.getCurrentObservation();
      }

      if (observation != null) {
        observation.observe(
            () -> log.error("Reactive error [{}]: {}", problem.getStatus(), ex.getMessage()));
      } else {
        log.error("Reactive error [{}]: {}", problem.getStatus(), ex.getMessage());
      }

      exchange.getResponse().setStatusCode(HttpStatus.valueOf(problem.getStatus()));
      exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_PROBLEM_JSON);

      return exchange
          .getResponse()
          .writeWith(
              Mono.fromCallable(() -> objectMapper.writeValueAsBytes(problem))
                  .map(exchange.getResponse().bufferFactory()::wrap));
    }
  }
}
