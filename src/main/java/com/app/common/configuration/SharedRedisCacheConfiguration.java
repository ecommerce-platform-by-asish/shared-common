package com.app.common.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/** Standard Redis caching configuration for shared microservice use. */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RedisCacheConfiguration.class)
public class SharedRedisCacheConfiguration {

  /** Provides a Redis-backed CacheManager as the primary caching provider. */
  @Bean
  @Primary
  @ConditionalOnClass(RedisConnectionFactory.class)
  public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    return RedisCacheManager.builder(connectionFactory)
        .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig())
        .build();
  }
}
