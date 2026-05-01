package com.app.common.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Automatically loads common default properties for all services that include shared-common.
 * This centralizes standard settings for logging, swagger, and reliability.
 */
@Configuration
@PropertySource("classpath:common-defaults.properties")
public class CommonDefaultsAutoConfiguration {
}
