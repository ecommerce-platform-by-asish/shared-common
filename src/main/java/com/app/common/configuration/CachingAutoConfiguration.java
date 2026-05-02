package com.app.common.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/** Activates Spring Cache abstraction with a safe fallback to No-Op caching. */
@AutoConfiguration
@EnableCaching
@ConditionalOnProperty(name = "app.caching.enabled", havingValue = "true")
@Import(SharedRedisCacheConfiguration.class)
public class CachingAutoConfiguration {

  /** Provides a No-Op CacheManager as a fallback to prevent application startup failures. */
  @Bean
  @ConditionalOnMissingBean(CacheManager.class)
  public CacheManager fallbackCacheManager() {
    return new NoOpCacheManager();
  }
}
