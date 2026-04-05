package com.ecommerce.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base class for all domain-specific exceptions. Uses HttpStatus and ErrorCode as the standardized
 * mapping for responses.
 */
@Getter
public abstract class BaseException extends RuntimeException {

  private final ErrorCode errorCode;

  protected BaseException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }

  protected BaseException(String message, ErrorCode errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  protected BaseException(String message, ErrorCode errorCode, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  protected BaseException(ErrorCode errorCode, Throwable cause) {
    super(errorCode.getMessage(), cause);
    this.errorCode = errorCode;
  }

  public HttpStatus getHttpStatus() {
    return errorCode.getStatus();
  }
}
