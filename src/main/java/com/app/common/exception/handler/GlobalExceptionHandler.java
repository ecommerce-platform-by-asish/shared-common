package com.app.common.exception.handler;

import com.app.common.exception.BaseException;
import com.app.common.exception.GlobalStatusCode;
import com.app.common.exception.StatusCode;
import com.app.common.exception.ValidationError;
import com.app.common.model.CommonConstants;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

/** Holder for User Identity filters that propagate Gateway headers into Security Contexts. */
public final class GlobalExceptionHandler {

  private GlobalExceptionHandler() {}

  /** Intercepts system exceptions to return standardized API responses in Servlet apps. */
  @Slf4j
  @RestControllerAdvice
  @ConditionalOnWebApplication(type = Type.SERVLET)
  public static class Servlet {

    /** Handles domain-specific business exceptions. */
    @ExceptionHandler(BaseException.class)
    public ProblemDetail handleBaseException(BaseException ex) {
      log.error("Business exception: {}", ex.getMessage());
      StatusCode statusCode = ex.getErrorCode();
      ProblemDetail problem =
          ProblemDetail.forStatusAndDetail(statusCode.getHttpStatus(), ex.getMessage());
      problem.setTitle(statusCode.toString());
      return problem;
    }

    /** Maps bean validation failures to detailed error reports. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
      List<ValidationError> errors =
          ex.getBindingResult().getFieldErrors().stream()
              .map(e -> new ValidationError(e.getField(), e.getDefaultMessage()))
              .toList();

      ProblemDetail problem =
          ProblemDetail.forStatusAndDetail(
              GlobalStatusCode.VALIDATION_ERROR.getHttpStatus(),
              GlobalStatusCode.VALIDATION_ERROR.getMessage());
      problem.setTitle(GlobalStatusCode.VALIDATION_ERROR.toString());
      problem.setProperty("errors", errors);
      return problem;
    }

    /** Handles database access and connectivity issues. */
    @Slf4j
    @RestControllerAdvice
    @ConditionalOnClass(DataAccessException.class)
    @ConditionalOnWebApplication(type = Type.SERVLET)
    public static class Database {

      @ExceptionHandler(DataAccessException.class)
      public ProblemDetail handleDataAccessException(DataAccessException ex) {
        log.warn("Data access error: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Invalid request parameters: " + ex.getMostSpecificCause().getMessage());
      }
    }

    /** Fallback handler for all uncaught system exceptions. */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(Exception ex) {
      log.error("Unhandled exception occurred", ex);
      ProblemDetail problem =
          ProblemDetail.forStatusAndDetail(
              GlobalStatusCode.INTERNAL_SERVER_ERROR.getHttpStatus(),
              GlobalStatusCode.INTERNAL_SERVER_ERROR.getMessage());
      problem.setTitle(GlobalStatusCode.INTERNAL_SERVER_ERROR.toString());
      return problem;
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
  @ConditionalOnWebApplication(type = Type.REACTIVE)
  public static class Reactive implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;
    private final ObservationRegistry observationRegistry;

    @Override
    public @NonNull Mono<Void> handle(@NonNull ServerWebExchange exchange, Throwable ex) {
      ProblemDetail problem =
          switch (ex) {
            case BaseException be -> {
              var p =
                  ProblemDetail.forStatusAndDetail(
                      be.getErrorCode().getHttpStatus(), be.getMessage());
              p.setTitle(be.getErrorCode().toString());
              yield p;
            }
            case ResponseStatusException rse ->
                ProblemDetail.forStatusAndDetail(rse.getStatusCode(), rse.getReason());
            default ->
                ProblemDetail.forStatusAndDetail(
                    HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
          };

      Observation observation =
          exchange.getAttribute(
              "org.springframework.cloud.gateway.support.ServerWebExchangeUtils.gatewayObservation");
      if (observation == null) observation = observationRegistry.getCurrentObservation();

      String userId =
          exchange.getAttributeOrDefault(CommonConstants.USER_ID_KEY, CommonConstants.NONE);
      Runnable logger =
          () -> log.error("Reactive error [{}]: {}", problem.getStatus(), ex.getMessage(), ex);
      Optional.ofNullable(observation)
          .map(obs -> obs.error(ex))
          .ifPresentOrElse(obs -> obs.observe(logger), logger);

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
