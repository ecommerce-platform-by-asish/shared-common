plugins {
    `java-library`
    id("io.spring.dependency-management") version "1.1.7"
    `maven-publish`
    id("com.diffplug.spotless") version "8.4.0"
}

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

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
    }
}

dependencies {
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.springframework.boot:spring-boot-starter-data-jpa")
    compileOnly("org.springframework.boot:spring-boot-starter-validation")
    compileOnly("org.springframework.boot:spring-boot-jackson")
    compileOnly("org.springdoc:springdoc-openapi-starter-webmvc-ui:${springDocVersion}")

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

tasks.named("build") {
    finalizedBy("publishToMavenLocal")
}

tasks.named("check") {
    dependsOn("spotlessCheck")
}
