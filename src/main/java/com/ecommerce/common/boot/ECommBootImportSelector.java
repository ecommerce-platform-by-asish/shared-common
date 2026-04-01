package com.ecommerce.common.boot;

import com.ecommerce.common.config.OpenApiAutoConfiguration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class ECommBootImportSelector implements ImportSelector {

  @Override
  public String[] selectImports(AnnotationMetadata importingClassMetadata) {
    Map<String, Object> attributes =
        importingClassMetadata.getAnnotationAttributes(ECommBootApplication.class.getName());

    List<String> imports = new ArrayList<>();

    if ((boolean) attributes.getOrDefault("enableOpenApi", true)) {
      imports.add(OpenApiAutoConfiguration.class.getName());
    }

    return imports.toArray(new String[0]);
  }
}
