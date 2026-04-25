package com.app.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

/** Standard error codes shared across all microservices. */
public enum GlobalStatusCode implements StatusCode {
  SUCCESS(HttpStatus.OK, "Operation completed successfully"),
  VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "One or more validation errors occurred"),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred"),
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "Invalid request parameters"),
  NOT_FOUND(HttpStatus.NOT_FOUND, "The requested resource was not found"),
  SERVICE_UNAVAILABLE(
      HttpStatus.SERVICE_UNAVAILABLE, "The requested service is currently unavailable");

  private final HttpStatusCode status;
  private final String message;

  GlobalStatusCode(HttpStatusCode status, String message) {
    this.status = status;
    this.message = message;
  }

  @Override
  public HttpStatusCode getStatus() {
    return status;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public HttpStatus getHttpStatus() {
    return (HttpStatus) status;
  }
}
