package com.ecommerce.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

  private final boolean success;
  private final T data;
  private final String message;
  private final Instant timestamp;

  private ApiResponse(boolean success, T data, String message) {
    this.success = success;
    this.data = data;
    this.message = message;
    this.timestamp = Instant.now();
  }

  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>(true, data, null);
  }

  public static <T> ApiResponse<T> ok(T data, String message) {
    return new ApiResponse<>(true, data, message);
  }

  public static <T> ApiResponse<T> ok(String message) {
    return new ApiResponse<>(true, null, message);
  }

  public static <T> ApiResponse<T> error(String errorMessage) {
    return new ApiResponse<>(false, null, errorMessage);
  }

  public static <T> ApiResponse<T> error(T data, String errorMessage) {
    return new ApiResponse<>(false, data, errorMessage);
  }
}
