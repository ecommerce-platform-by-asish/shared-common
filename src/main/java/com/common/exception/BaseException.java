package com.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/** Base exception for the platform, ensuring consistent error reporting across services. */
@Getter
public abstract class BaseException extends RuntimeException {

  private final StatusCode errorCode;

  protected BaseException(StatusCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  protected BaseException(String message, StatusCode errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  protected BaseException(String message, StatusCode errorCode, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  protected BaseException(StatusCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
  }

  public HttpStatus getHttpStatus() {
    return errorCode.getStatus();
  }
}
