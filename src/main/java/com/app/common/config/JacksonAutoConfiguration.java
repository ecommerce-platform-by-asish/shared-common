package com.app.common.config;

import static com.app.common.constants.DateTimeConstants.DATETIME_FORMAT;
import static com.app.common.constants.DateTimeConstants.DATE_FORMAT;
import static com.app.common.constants.DateTimeConstants.TIME_FORMAT;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer;
import tools.jackson.databind.module.SimpleModule;

/** Standard Jackson configuration for consistent JSON processing. */
@AutoConfiguration
public class JacksonAutoConfiguration {

  @Bean
  public JsonMapperBuilderCustomizer jacksonCustomizer() {
    return builder -> {
      builder.addModule(createJavaTimeModule());
      builder.defaultDateFormat(new SimpleDateFormat(DATETIME_FORMAT));
      builder.defaultTimeZone(TimeZone.getTimeZone("UTC"));
      builder.disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
    };
  }

  public static SimpleModule createJavaTimeModule() {
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATETIME_FORMAT);
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_FORMAT);

    SimpleModule module = new SimpleModule();

    module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
    module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));

    module.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
    module.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));

    module.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));
    module.addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormatter));

    return module;
  }
}
