package com.app.common.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/** Centralized Actuator configuration for health monitoring and metrics aggregation. */
@Configuration
@PropertySource("classpath:common-actuator.properties")
@ConditionalOnProperty(name = "app.actuator.enabled", havingValue = "true")
public class ActuatorAutoConfiguration {}
