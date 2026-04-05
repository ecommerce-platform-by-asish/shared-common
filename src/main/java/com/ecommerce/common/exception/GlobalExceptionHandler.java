package com.ecommerce.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@Order
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ProblemDetail> handleAllExceptions(
      Exception ex, HttpServletRequest request) {
    log.error("Internal Server Error occurred at path: {}", request.getRequestURI(), ex);
    var problem =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    problem.setTitle(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    return createResponse(problem, request, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(BaseException.class)
  public ResponseEntity<ProblemDetail> handleBaseException(
      BaseException ex, HttpServletRequest request) {
    log.warn("Handled {} at path: {}", ex.getClass().getSimpleName(), request.getRequestURI());
    var problem = ProblemDetail.forStatusAndDetail(ex.getHttpStatus(), ex.getMessage());
    problem.setTitle(ex.getHttpStatus().getReasonPhrase());
    return createResponse(problem, request, ex.getHttpStatus());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    log.warn("Validation failed at path: {}", request.getRequestURI());

    var problem =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "One or more validation errors occurred");
    problem.setTitle("Validation Failed");

    var errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(
                fe ->
                    new ValidationError(
                        fe.getField(), fe.getDefaultMessage(), fe.getRejectedValue()))
            .collect(Collectors.toList());

    problem.setProperty("invalid_params", errors);

    return createResponse(problem, request, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ProblemDetail> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException ex, HttpServletRequest request) {
    log.warn("Malformed JSON request at path: {}", request.getRequestURI());
    var problem =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Malformed or non-readable JSON request body");
    problem.setTitle(HttpStatus.BAD_REQUEST.getReasonPhrase());
    return createResponse(problem, request, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ProblemDetail> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
    log.warn("Argument type mismatch at path: {}", request.getRequestURI());
    var problem =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Invalid format for parameter: " + ex.getName());
    problem.setTitle(HttpStatus.BAD_REQUEST.getReasonPhrase());
    return createResponse(problem, request, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ProblemDetail> handleIllegalArgumentException(
      IllegalArgumentException ex, HttpServletRequest request) {
    log.warn("Illegal argument at path: {}: {}", request.getRequestURI(), ex.getMessage());
    var problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    problem.setTitle(HttpStatus.BAD_REQUEST.getReasonPhrase());
    return createResponse(problem, request, HttpStatus.BAD_REQUEST);
  }

  private ResponseEntity<ProblemDetail> createResponse(
      ProblemDetail problem, HttpServletRequest request, HttpStatus status) {
    problem.setInstance(URI.create(request.getRequestURI()));

    return new ResponseEntity<>(problem, status);
  }
}
