package com.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/** Common tracing and observation configuration. */
@Configuration
@PropertySource("classpath:common-tracing.properties")
public class TracingAutoConfiguration {}
