package com.ecommerce.common.exception;

import com.ecommerce.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Global Exception Handler for all services. Relies on the HTTP response header for the status
 * code.
 */
@Slf4j
@Order
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleAllExceptions(
      Exception ex, HttpServletRequest request) {
    log.error("Internal Server Error occurred at path: {}", request.getRequestURI(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error(GlobalErrorCode.INTERNAL_SERVER_ERROR));
  }

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<ApiResponse<Void>> handleBaseException(
      BaseException ex, HttpServletRequest request) {
    log.warn("Handled {} at path: {}", ex.getClass().getSimpleName(), request.getRequestURI());
    return ResponseEntity.status(ex.getHttpStatus())
        .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    log.warn("Validation failed at path: {}", request.getRequestURI());

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
      ConstraintViolationException x, HttpServletRequest request) {
    log.warn("Constraint violation at path: {}", request.getRequestURI());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(GlobalErrorCode.BAD_REQUEST, x.getMessage()));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex, HttpServletRequest request) {
    log.warn("Malformed JSON request at path: {}", request.getRequestURI());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ApiResponse.error(
                GlobalErrorCode.BAD_REQUEST, "Malformed or non-readable JSON request body"));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
    log.warn("Argument type mismatch at path: {}", request.getRequestURI());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ApiResponse.error(
                GlobalErrorCode.BAD_REQUEST, "Invalid format for parameter: " + ex.getName()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
      IllegalArgumentException ex, HttpServletRequest request) {
    log.warn("Illegal argument at path: {}: {}", request.getRequestURI(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(GlobalErrorCode.BAD_REQUEST, ex.getMessage()));
  }
}
