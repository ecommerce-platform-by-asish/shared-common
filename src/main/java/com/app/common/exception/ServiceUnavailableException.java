package com.app.common.exception;

/** Exception thrown when a downstream microservice is unavailable or circuit breaker is open. */
public class ServiceUnavailableException extends BaseException {

  public ServiceUnavailableException() {
    super(GlobalStatusCode.SERVICE_UNAVAILABLE);
  }

  public ServiceUnavailableException(String message) {
    super(message, GlobalStatusCode.SERVICE_UNAVAILABLE);
  }
}
