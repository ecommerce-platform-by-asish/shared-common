package com.app.common.mapper;

import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Shared MapStruct configuration to standardize mapping behavior across all microservices.
 * Automatically ignores common auditing fields defined in BaseEntity.
 */
@MapperConfig(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BaseMapperConfig {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "isActive", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "version", ignore = true)
  Object anyToEntity(Object dto);
}
