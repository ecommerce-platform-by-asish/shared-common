package com.common.config;

import com.common.exception.GlobalExceptionHandler;
import com.common.exception.ReactiveGlobalExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

/** Auto-configuration for global exception handlers. */
@AutoConfiguration
@Import({GlobalExceptionHandler.class, ReactiveGlobalExceptionHandler.class})
public class ExceptionAutoConfiguration {}
