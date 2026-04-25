plugins {
    `java-library`
    `maven-publish`
    
    alias(libs.plugins.spotless)
}

group = "com.app"
version = "1.0.0-SNAPSHOT"
description = "Core infrastructure library providing base Spring Boot application features, tracing, and observation."

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<Javadoc> {
    (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
}

dependencies {
    
    api(platform(libs.spring.boot.bom))
    api(platform(libs.jackson.bom))

    api(libs.spring.boot.starter)
    api(libs.spring.boot.starter.validation)
    api(libs.spring.boot.starter.actuator)
    api(libs.spring.boot.autoconfigure)

    api(libs.jackson.annotations)
    implementation(libs.jackson.databind)

    implementation(libs.spring.boot.starter.web)
    api(libs.spring.boot.starter.webflux)
    implementation(libs.spring.boot.starter.json)
    
    compileOnly(libs.spring.boot.starter.data.jpa)
    compileOnly(libs.springdoc.openapi.webmvc)
    compileOnly(libs.spring.boot.starter.data.redis)

    api(libs.micrometer.observation)
    api(libs.micrometer.tracing)
    api(libs.micrometer.tracing.bridge.otel)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    annotationProcessor(platform(libs.spring.boot.bom))
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
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

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.isFork = true
    options.forkOptions.jvmArgs = (options.forkOptions.jvmArgs ?: mutableListOf()).apply {
        addAll(listOf(
            "--add-opens", "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
            "--add-opens", "jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED",
            "--add-opens", "jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
            "--add-opens", "jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
            "--add-opens", "jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED",
            "--add-opens", "jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
            "--add-opens", "jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED",
            "--add-opens", "jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
            "--add-opens", "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"
        ))
    }
    options.compilerArgs.addAll(listOf(
        "-Xlint:all", "-Xlint:-serial", "-Xlint:-processing", "-Xdoclint:none"
    ))
}

spotless {
    java {
        googleJavaFormat("1.27.0")
    }
}

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.bouncycastle" && requested.name.startsWith("bcprov")) {
            useVersion("1.84")
            because("Force upgrade to resolve CVE-2026-0636")
        }
    }
}
