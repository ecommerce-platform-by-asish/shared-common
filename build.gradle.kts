plugins {
    `java-library`
    id("io.spring.dependency-management") version "1.1.7"
    `maven-publish`
    id("com.diffplug.spotless") version "8.4.0"
}
 
group = "com.common"
version = "1.0.0-SNAPSHOT"
description = "Core infrastructure library providing base Spring Boot application features, tracing, and observation."


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
}

spotless {
    java {
        googleJavaFormat()
    }
}

val springBootVersion = "4.0.5"
val springDocVersion = "2.8.6"
val micrometerTracingVersion = "1.6.4"      // resolved from Spring Boot 4.0.5 BOM
val openTelemetryVersion = "1.55.0"         // resolved from Spring Boot 4.0.5 BOM

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
    }
}

dependencies {
    // Promoted to `api` scope — every service inherits these automatically
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.boot:spring-boot-starter-actuator")

    // Optional — only pulled in by services that explicitly need them
    compileOnly("org.springframework.boot:spring-boot-starter-webflux")
    compileOnly("org.springframework.boot:spring-boot-starter-data-jpa")
    compileOnly("org.springframework.boot:spring-boot-jackson")
    compileOnly("org.springdoc:springdoc-openapi-starter-webmvc-ui:${springDocVersion}")
    compileOnly("org.springframework.boot:spring-boot-starter-data-redis")

    // OTel tracing — transitive to all consumers via `api` scope
    // Versions pinned explicitly so Maven metadata validation passes (they come from the BOM)
    api("io.micrometer:micrometer-observation")
    api("io.micrometer:micrometer-tracing")
    api("io.micrometer:micrometer-tracing-bridge-otel:${micrometerTracingVersion}")
    api("io.opentelemetry:opentelemetry-sdk:${openTelemetryVersion}")
    api("io.opentelemetry:opentelemetry-sdk-extension-autoconfigure:${openTelemetryVersion}")
    api("io.opentelemetry:opentelemetry-exporter-logging")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

configurations.all {
    // Jackson 3.x moved core/databind to tools.jackson — exclude old 2.x artifacts.
    // jackson-annotations is still com.fasterxml and is needed by Jackson 3.x.
    exclude(group = "com.fasterxml.jackson.core", module = "jackson-core")
    exclude(group = "com.fasterxml.jackson.core", module = "jackson-databind")
    exclude(group = "com.fasterxml.jackson.datatype")
    exclude(group = "com.fasterxml.jackson.module")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

tasks.named("build") {
    finalizedBy("publishToMavenLocal")
}

tasks.named("check") {
    dependsOn("spotlessCheck")
}
