package com.app.common.exception;

import java.io.Serializable;
import org.springframework.http.HttpStatusCode;

/**
 * Standard interface for all system machine-readable codes. This can be implemented by enums in
 * different services to provide extensible business codes and their default HTTP status.
 */
public interface StatusCode extends Serializable {
  HttpStatusCode getStatus();

  default org.springframework.http.HttpStatus getHttpStatus() {
    return org.springframework.http.HttpStatus.valueOf(getStatus().value());
  }

  String getMessage();

  default int getStatusCode() {
    return getStatus().value();
  }
}
