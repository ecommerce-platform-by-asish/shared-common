package com.ecommerce.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum GlobalErrorCode implements ErrorCode {
  SUCCESS(HttpStatus.OK, "Operation completed successfully"),
  VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "One or more validation errors occurred"),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred"),
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "Invalid request parameters"),
  NOT_FOUND(HttpStatus.NOT_FOUND, "The requested resource was not found");

  private final HttpStatus status;
  private final String message;

  GlobalErrorCode(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }
}
