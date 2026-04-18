package com.app.common.web.dto;

import com.app.common.exception.GlobalStatusCode;
import com.app.common.exception.StatusCode;
import com.app.common.exception.ValidationError;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/** Standardized API response wrapper for all microservice endpoints. */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

  private final boolean success;
  private final String code;
  private final T data;
  private final String message;
  private final List<ValidationError> errors;
  @Builder.Default private final Instant timestamp = Instant.now();

  public static <T> ApiResponse<T> ok(T data) {
    return ok(data, GlobalStatusCode.SUCCESS.getMessage());
  }

  public static <T> ApiResponse<T> ok(T data, String message) {
    return ApiResponse.<T>builder()
        .success(true)
        .code(GlobalStatusCode.SUCCESS.toString())
        .data(data)
        .message(message)
        .build();
  }

  public static <T> ApiResponse<T> created(T data, String message) {
    return ApiResponse.<T>builder()
        .success(true)
        .code(GlobalStatusCode.SUCCESS.toString())
        .data(data)
        .message(message)
        .build();
  }

  public static <T> ApiResponse<T> error(StatusCode errorCode) {
    return error(errorCode, errorCode.getMessage());
  }

  public static <T> ApiResponse<T> error(StatusCode errorCode, String message) {
    return ApiResponse.<T>builder()
        .success(false)
        .code(errorCode.toString())
        .message(message)
        .build();
  }

  public static ApiResponse<Void> error(
      String message, StatusCode errorCode, List<ValidationError> errors) {
    return ApiResponse.<Void>builder()
        .success(false)
        .code(errorCode.toString())
        .message(message)
        .errors(errors)
        .build();
  }

  public static <T> ApiResponse<T> error(
      String message, String code, List<ValidationError> errors) {
    return ApiResponse.<T>builder()
        .success(false)
        .code(code)
        .message(message)
        .errors(errors)
        .build();
  }
}
