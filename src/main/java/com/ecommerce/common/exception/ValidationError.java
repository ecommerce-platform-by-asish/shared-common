package com.ecommerce.common.exception;

public record ValidationError(String field, String message, Object rejectedValue) {}
