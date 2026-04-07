package com.app.common.exception;

/** Single field validation error detail. */
public record ValidationError(String field, String message, Object rejectedValue) {}
