package com.app.common.exception;

import com.app.common.web.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Standard error handler for all microservices in the platform. */
@Slf4j
@Order
@ControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleAllExceptions(Exception ex, WebRequest request) {
    log.error("Unhandled {} at path: {}", ex.getClass().getName(), request.getContextPath(), ex);
    return ResponseEntity.ok().body(ApiResponse.error(GlobalStatusCode.INTERNAL_SERVER_ERROR));
  }

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<ApiResponse<Void>> handleBaseException(
      BaseException ex, WebRequest request) {
    log.warn("Handled {} at path: {}", ex.getClass().getSimpleName(), request.getContextPath());
    return ResponseEntity.ok().body(ApiResponse.error(ex.getErrorCode(), ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex, WebRequest request) {
    log.warn("Validation failed at path: {}", request.getContextPath());

    var errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(
                fe ->
                    new ValidationError(
                        fe.getField(), fe.getDefaultMessage(), fe.getRejectedValue()))
            .collect(Collectors.toList());

    return ResponseEntity.ok()
        .body(
            ApiResponse.error(
                GlobalStatusCode.VALIDATION_ERROR.getMessage(),
                GlobalStatusCode.VALIDATION_ERROR,
                errors));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(
      ConstraintViolationException x, WebRequest request) {
    log.warn("Constraint violation at path: {}", request.getContextPath());
    return ResponseEntity.ok()
        .body(ApiResponse.error(GlobalStatusCode.BAD_REQUEST, x.getMessage()));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex, WebRequest request) {
    log.warn("Malformed JSON request at path: {}", request.getContextPath());
    return ResponseEntity.ok()
        .body(
            ApiResponse.error(
                GlobalStatusCode.BAD_REQUEST, "Malformed or non-readable JSON request body"));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex, WebRequest request) {
    log.warn("Argument type mismatch at path: {}", request.getContextPath());
    return ResponseEntity.ok()
        .body(
            ApiResponse.error(
                GlobalStatusCode.BAD_REQUEST, "Invalid format for parameter: " + ex.getName()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {
    log.warn("Illegal argument at path: {}: {}", request.getContextPath(), ex.getMessage());
    return ResponseEntity.ok()
        .body(ApiResponse.error(GlobalStatusCode.BAD_REQUEST, ex.getMessage()));
  }
}
