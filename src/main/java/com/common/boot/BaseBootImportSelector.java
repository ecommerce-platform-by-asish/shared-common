package com.common.boot;

import com.common.config.ActuatorAutoConfiguration;
import com.common.config.OpenApiAutoConfiguration;
import com.common.config.RedisCacheAutoConfiguration;
import com.common.config.TracingAutoConfiguration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/** Selects additional configurations based on annotation attributes. */
class BaseBootImportSelector implements ImportSelector {

  @Override
  public String @NonNull [] selectImports(AnnotationMetadata importingClassMetadata) {
    Map<String, Object> attributes =
        importingClassMetadata.getAnnotationAttributes(BaseSpringBootApplication.class.getName());

    if (attributes == null) {
      return new String[0];
    }

    List<String> imports = new ArrayList<>();

    // Always register tracing & observation defaults
    imports.add(TracingAutoConfiguration.class.getName());

    if ((boolean) attributes.getOrDefault("enableOpenApi", false)) {
      imports.add(OpenApiAutoConfiguration.class.getName());
    }

    if ((boolean) attributes.getOrDefault("enableActuator", false)) {
      imports.add(ActuatorAutoConfiguration.class.getName());
    }

    if ((boolean) attributes.getOrDefault("enableCaching", false)) {
      imports.add(RedisCacheAutoConfiguration.class.getName());
    }

    return imports.toArray(new String[0]);
  }
}
