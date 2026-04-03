package com.ecommerce.common.boot;

import com.ecommerce.common.config.OpenApiAutoConfiguration;
import com.ecommerce.common.config.RedisCacheAutoConfiguration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

class EcomBootImportSelector implements ImportSelector {

  @Override
  public String @NonNull [] selectImports(AnnotationMetadata importingClassMetadata) {
    Map<String, Object> attributes =
        importingClassMetadata.getAnnotationAttributes(EcomBootApplication.class.getName());

    if (attributes == null) {
      return new String[0];
    }

    List<String> imports = new ArrayList<>();

    if ((boolean) attributes.getOrDefault("enableOpenApi", false)) {
      imports.add(OpenApiAutoConfiguration.class.getName());
    }

    if ((boolean) attributes.getOrDefault("enableActuator", false)) {
      imports.add("com.ecommerce.common.config.ActuatorAutoConfiguration");
    }

    if ((boolean) attributes.getOrDefault("enableCaching", false)) {
      imports.add(RedisCacheAutoConfiguration.class.getName());
    }

    return imports.toArray(new String[0]);
  }
}
