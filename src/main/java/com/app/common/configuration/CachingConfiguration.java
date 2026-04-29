package com.app.common.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Enables Spring Cache abstraction with a fallback. */
@Configuration
@EnableCaching
@ConditionalOnProperty(name = "app.caching.enabled", havingValue = "true")
public class CachingConfiguration {

  @Bean
  @ConditionalOnMissingBean(CacheManager.class)
  public CacheManager fallbackCacheManager() {
    return new NoOpCacheManager();
  }
}
