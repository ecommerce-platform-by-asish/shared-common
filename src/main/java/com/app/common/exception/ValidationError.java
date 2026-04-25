package com.app.common.exception;

import java.io.Serializable;

/** Model for individual field validation errors. */
public record ValidationError(String field, String message) implements Serializable {}
