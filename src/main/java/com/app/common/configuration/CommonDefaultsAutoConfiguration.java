package com.app.common.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Automatically loads common default properties for all services that include shared-common. This
 * centralizes standard settings for logging, swagger, and reliability.
 */
@AutoConfiguration
@PropertySource("classpath:common-defaults.properties")
public class CommonDefaultsAutoConfiguration {
  @Bean
  @ConditionalOnMissingBean
  public ObjectMapper defaultObjectMapper() {
    return JsonMapper.builder().findAndAddModules().build();
  }
}
