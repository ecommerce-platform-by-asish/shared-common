package com.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/** Shared Actuator configuration for health monitoring and metrics. */
@Configuration
@PropertySource("classpath:common-actuator.properties")
public class ActuatorAutoConfiguration {}
