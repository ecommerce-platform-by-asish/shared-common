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
val springDocVersion = "3.0.2"

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
    }
}

dependencies {
    // Platform Infrastructure — every service inherits these automatically
    api("org.springframework.boot:spring-boot-starter")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.boot:spring-boot-starter-actuator")

    // Optional Selective Stacks — must be explicitly added by the service
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.springframework.boot:spring-boot-starter-webflux")
    compileOnly("org.springframework.boot:spring-boot-starter-data-jpa")
    compileOnly("org.springframework.boot:spring-boot-jackson")
    compileOnly("org.springdoc:springdoc-openapi-starter-webmvc-ui:${springDocVersion}")
    compileOnly("org.springframework.boot:spring-boot-starter-data-redis")

    // OTel tracing — transitive to all consumers via `api` scope
    api("org.springframework.boot:spring-boot-starter-opentelemetry")
    api("io.micrometer:micrometer-observation")
    api("io.micrometer:micrometer-tracing")
    api("io.micrometer:micrometer-tracing-bridge-otel")
    api("io.micrometer:context-propagation")
    
    // Additional OTel exporters pulled in for customization
    api("io.opentelemetry:opentelemetry-exporter-logging")
    api("io.opentelemetry:opentelemetry-exporter-otlp")

    // SQL Log Formatting and Inlined Value Tracing
    api("com.github.gavlyukovskiy:p6spy-spring-boot-starter:2.0.0")

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

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:all")
    options.compilerArgs.add("-Xlint:-processing") // suppress lombok annotation processor warnings if any
}

tasks.named("build") {
    finalizedBy("publishToMavenLocal")
}

tasks.named("check") {
    dependsOn("spotlessCheck")
}
