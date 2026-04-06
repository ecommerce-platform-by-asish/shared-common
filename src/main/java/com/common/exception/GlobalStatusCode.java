package com.common.exception;

/** Standard error codes shared across all microservices. */
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GlobalStatusCode implements StatusCode {
  SUCCESS(HttpStatus.OK, "Operation completed successfully"),
  VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "One or more validation errors occurred"),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred"),
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "Invalid request parameters"),
  NOT_FOUND(HttpStatus.NOT_FOUND, "The requested resource was not found"),
  SERVICE_UNAVAILABLE(
      HttpStatus.SERVICE_UNAVAILABLE, "The requested service is currently unavailable");

  private final HttpStatus status;
  private final String message;
}
