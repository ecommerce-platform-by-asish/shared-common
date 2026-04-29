package com.app.common.dto;

import com.app.common.exception.GlobalStatusCode;
import com.app.common.exception.StatusCode;
import com.app.common.exception.ValidationError;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/** Standardized API response wrapper for all microservice endpoints. */
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

  public static <T> ApiResponse<T> ok(T data) {
    return ok(data, GlobalStatusCode.SUCCESS.getMessage());
  }

  public static <T> ApiResponse<T> ok(T data, String message) {
    return new ApiResponse<>(true, GlobalStatusCode.SUCCESS.toString(), data, message, null, null);
  }

  public static <T> ApiResponse<T> error(StatusCode errorCode) {
    return error(errorCode, errorCode.getMessage());
  }

  public static <T> ApiResponse<T> error(StatusCode errorCode, String message) {
    return new ApiResponse<>(false, errorCode.toString(), null, message, null, null);
  }

  public static ApiResponse<Void> error(
      String message, StatusCode errorCode, List<ValidationError> errors) {
    return new ApiResponse<>(false, errorCode.toString(), null, message, errors, null);
  }

  public static <T> ApiResponse<T> error(
      String message, String code, List<ValidationError> errors) {
    return new ApiResponse<>(false, code, null, message, errors, null);
  }

  public ResponseEntity<ApiResponse<T>> toEntity() {
    return toEntity(HttpStatus.OK);
  }

  public ResponseEntity<ApiResponse<T>> toEntity(HttpStatus status) {
    return ResponseEntity.status(status).body(this);
  }
}
