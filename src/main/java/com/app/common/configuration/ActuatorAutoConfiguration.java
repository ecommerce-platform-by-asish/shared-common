package com.app.common.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/** Shared Actuator configuration for health monitoring and metrics. */
@Configuration
@PropertySource("classpath:common-actuator.properties")
@ConditionalOnProperty(name = "app.actuator.enabled", havingValue = "true")
public class ActuatorAutoConfiguration {}
