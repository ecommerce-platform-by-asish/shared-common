package com.ecommerce.common.exception;

import org.springframework.http.HttpStatus;

/**
 * Standard interface for all system machine-readable codes. This can be implemented by enums in
 * different services to provide extensible business codes and their default HTTP status.
 */
public interface ErrorCode {
  HttpStatus getStatus();

  String getMessage();

  default int getStatusCode() {
    return getStatus().value();
  }
}
