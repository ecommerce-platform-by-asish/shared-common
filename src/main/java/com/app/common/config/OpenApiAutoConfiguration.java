package com.app.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Unified OpenAPI/Swagger configuration for API documentation. */
@Configuration
@ConditionalOnClass(name = "org.springdoc.core.models.GroupedOpenApi")
public class OpenApiAutoConfiguration {

  @Value("${app.openapi.title:Common Service API}")
  private String title;

  @Value("${app.openapi.description:Microservice API}")
  private String description;

  @Value("${app.openapi.version:1.0.0}")
  private String version;

  private static final String SECURITY_SCHEME_NAME = "BearerAuth";

  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title(title)
                .description(description)
                .version(version)
                .contact(
                    new Contact()
                        .name("Common Platform")
                        .url("https://github.com.app-platform-by-asish"))
                .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")))
        .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
        .components(
            new Components()
                .addSecuritySchemes(
                    SECURITY_SCHEME_NAME,
                    new SecurityScheme()
                        .name(SECURITY_SCHEME_NAME)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("Paste the JWT token from /api/auth/login")));
  }
}
