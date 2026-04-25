package com.app.common.exception.handler;

import com.app.common.dto.ApiResponse;
import com.app.common.exception.BaseException;
import com.app.common.exception.GlobalStatusCode;
import com.app.common.exception.StatusCode;
import com.app.common.exception.ValidationError;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Central exception handler for Servlet-based (MVC) applications. */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException ex) {
    log.error("Business exception: {}", ex.getMessage());
    StatusCode statusCode = ex.getErrorCode();
    ApiResponse<Void> response = ApiResponse.error(statusCode);
    return new ResponseEntity<>(response, statusCode.getHttpStatus());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidationException(
      MethodArgumentNotValidException ex) {
    List<ValidationError> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(this::mapToValidationError)
            .collect(Collectors.toList());

    ApiResponse<Void> response =
        ApiResponse.error(
            GlobalStatusCode.VALIDATION_ERROR.getMessage(),
            GlobalStatusCode.VALIDATION_ERROR.toString(),
            errors);

    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
    log.error("Unhandled exception occurred", ex);
    ApiResponse<Void> response = ApiResponse.error(GlobalStatusCode.INTERNAL_SERVER_ERROR);
    return new ResponseEntity<>(response, GlobalStatusCode.INTERNAL_SERVER_ERROR.getHttpStatus());
  }

  private ValidationError mapToValidationError(FieldError error) {
    return new ValidationError(error.getField(), error.getDefaultMessage());
  }
}
