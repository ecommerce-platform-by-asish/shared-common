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
}

spotless {
    java {
        googleJavaFormat()
    }
}

val springBootVersion = "4.0.5"

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
    api("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

tasks.named("build") {
    finalizedBy("publishToMavenLocal")
}

tasks.named("check") {
    dependsOn("spotlessCheck")
}
