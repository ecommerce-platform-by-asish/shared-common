package com.ecommerce.common.config;

import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConditionalOnClass(WebEndpointProperties.class)
@PropertySource("classpath:actuator-defaults.properties")
public class ActuatorAutoConfiguration {}
