package com.app.common.dto;

import com.app.common.exception.GlobalStatusCode;
import com.app.common.exception.StatusCode;
import com.app.common.exception.ValidationError;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/** Standardized API response structure for consistent client communication. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    boolean success,
    String code,
    T data,
    String message,
    List<ValidationError> errors,
    Instant timestamp) {

  public ApiResponse {
    timestamp = (timestamp == null) ? Instant.now() : timestamp;
  }

  /** Creates a successful response with the default message. */
  public static <T> ApiResponse<T> ok(T data) {
    return ok(data, GlobalStatusCode.SUCCESS.getMessage());
  }

  public static <T> ApiResponse<T> ok(T data, String message) {
    return new ApiResponse<>(true, GlobalStatusCode.SUCCESS.toString(), data, message, null, null);
  }

  /** Creates an error response from a standardized status code. */
  public static <T> ApiResponse<T> error(StatusCode errorCode) {
    return error(errorCode, errorCode.getMessage());
  }

  /** Creates a custom error response with a manual message and code. */
  public static <T> ApiResponse<T> error(StatusCode errorCode, String message) {
    return new ApiResponse<>(false, errorCode.toString(), null, message, null, null);
  }

  /** Creates a detailed validation error response. */
  public static ApiResponse<Void> error(
      String message, StatusCode errorCode, List<ValidationError> errors) {
    return new ApiResponse<>(false, errorCode.toString(), null, message, errors, null);
  }

  /** Creates a generic error response with a string-based code. */
  public static <T> ApiResponse<T> error(
      String message, String code, List<ValidationError> errors) {
    return new ApiResponse<>(false, code, null, message, errors, null);
  }

  /** Wraps this response into a ResponseEntity with HTTP 200. */
  public ResponseEntity<ApiResponse<T>> toEntity() {
    return toEntity(HttpStatus.OK);
  }

  /** Wraps this response into a ResponseEntity with a custom status. */
  public ResponseEntity<ApiResponse<T>> toEntity(HttpStatus status) {
    return ResponseEntity.status(status).body(this);
  }
}
