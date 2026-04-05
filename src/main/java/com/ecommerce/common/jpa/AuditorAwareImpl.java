package com.ecommerce.common.jpa;

import java.util.Optional;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.AuditorAware;

public class AuditorAwareImpl implements AuditorAware<String> {

  @Override
  public @NonNull Optional<String> getCurrentAuditor() {
    return Optional.of("SYSTEM");
  }
}
