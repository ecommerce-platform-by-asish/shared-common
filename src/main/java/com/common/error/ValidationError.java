package com.common.error;

/** Single field validation error detail. */
public record ValidationError(String field, String message, Object rejectedValue) {}
