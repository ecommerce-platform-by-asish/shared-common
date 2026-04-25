package com.app.common.boot;

import com.app.common.configuration.ActuatorAutoConfiguration;
import com.app.common.configuration.CachingConfiguration;
import com.app.common.configuration.OpenApiAutoConfiguration;
import com.app.common.configuration.RedisCacheAutoConfiguration;
import com.app.common.configuration.TracingAutoConfiguration;
import java.util.Map;
import java.util.stream.Stream;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/** Selects additional configurations based on annotation attributes. */
class BaseBootImportSelector implements ImportSelector {

  private record ImportEntry(String flag, Class<?> configClass) {}

  @Override
  public String @NonNull [] selectImports(AnnotationMetadata importingClassMetadata) {
    Map<String, Object> attributes =
        importingClassMetadata.getAnnotationAttributes(BaseSpringBootApplication.class.getName());

    if (attributes == null) {
      return new String[0];
    }

    return Stream.concat(
            Stream.of(TracingAutoConfiguration.class),
            Stream.of(
                    new ImportEntry("enableOpenApi", OpenApiAutoConfiguration.class),
                    new ImportEntry("enableActuator", ActuatorAutoConfiguration.class),
                    new ImportEntry("enableCaching", CachingConfiguration.class),
                    new ImportEntry("enableCaching", RedisCacheAutoConfiguration.class))
                .filter(e -> (boolean) attributes.getOrDefault(e.flag(), false))
                .map(ImportEntry::configClass))
        .map(Class::getName)
        .toArray(String[]::new);
  }
}
