package com.common.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.module.SimpleModule;

/** Standard Redis caching configuration for microservices. */
@Configuration
@ConditionalOnClass(name = "org.springframework.data.redis.cache.RedisCacheConfiguration")
@EnableCaching
public class RedisCacheAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public RedisCacheConfiguration redisCacheConfiguration() {
    PolymorphicTypeValidator ptv =
        BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).build();

    SimpleModule javaTimeModule = JacksonAutoConfiguration.createJavaTimeModule();

    JsonMapper jsonMapper =
        JsonMapper.builder()
            .addModule(javaTimeModule)
            .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
            .activateDefaultTyping(ptv, DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
            .build();

    GenericJacksonJsonRedisSerializer serializer =
        new GenericJacksonJsonRedisSerializer(jsonMapper);

    return RedisCacheConfiguration.defaultCacheConfig()
        .serializeKeysWith(
            RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()))
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(serializer));
  }
}
