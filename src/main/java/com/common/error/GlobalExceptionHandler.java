package com.common.error;

import com.common.web.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
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
    log.error("Internal Server Error occurred at path: {}", request.getContextPath(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error(GlobalErrorCode.INTERNAL_SERVER_ERROR));
  }

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<ApiResponse<Void>> handleBaseException(
      BaseException ex, WebRequest request) {
    log.warn("Handled {} at path: {}", ex.getClass().getSimpleName(), request.getContextPath());
    return ResponseEntity.status(ex.getHttpStatus())
        .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage()));
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

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ApiResponse.error(
                GlobalErrorCode.VALIDATION_ERROR.getMessage(),
                GlobalErrorCode.VALIDATION_ERROR,
                errors));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(
      ConstraintViolationException x, WebRequest request) {
    log.warn("Constraint violation at path: {}", request.getContextPath());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(GlobalErrorCode.BAD_REQUEST, x.getMessage()));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex, WebRequest request) {
    log.warn("Malformed JSON request at path: {}", request.getContextPath());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ApiResponse.error(
                GlobalErrorCode.BAD_REQUEST, "Malformed or non-readable JSON request body"));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex, WebRequest request) {
    log.warn("Argument type mismatch at path: {}", request.getContextPath());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ApiResponse.error(
                GlobalErrorCode.BAD_REQUEST, "Invalid format for parameter: " + ex.getName()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
      IllegalArgumentException ex, WebRequest request) {
    log.warn("Illegal argument at path: {}: {}", request.getContextPath(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(GlobalErrorCode.BAD_REQUEST, ex.getMessage()));
  }
}
