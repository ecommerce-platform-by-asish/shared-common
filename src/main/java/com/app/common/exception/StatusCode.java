package com.app.common.exception;

import java.io.Serializable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

/** Interface for all machine-readable business status codes and their associated HTTP status. */
public interface StatusCode extends Serializable {
  HttpStatusCode getStatus();

  default HttpStatus getHttpStatus() {
    return HttpStatus.valueOf(getStatus().value());
  }

  String getMessage();

  default int getStatusCode() {
    return getStatus().value();
  }
}
