package com.app.common.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/** Standard Redis caching configuration. */
@Configuration
@ConditionalOnClass(RedisCacheConfiguration.class)
public class RedisCacheAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public RedisCacheConfiguration redisCacheConfiguration() {
    ObjectMapper objectMapper = JsonMapper.builder().build();
    GenericJacksonJsonRedisSerializer serializer =
        new GenericJacksonJsonRedisSerializer(objectMapper);

    return RedisCacheConfiguration.defaultCacheConfig()
        .serializeKeysWith(
            RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(serializer));
  }

  @Bean
  @Primary
  @ConditionalOnClass(RedisConnectionFactory.class)
  public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    return RedisCacheManager.builder(connectionFactory)
        .cacheDefaults(redisCacheConfiguration())
        .build();
  }
}
