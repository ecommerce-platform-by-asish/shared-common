plugins {
    id("java-library")
    id("maven-publish")
    alias(libs.plugins.springboot)
    alias(libs.plugins.spotless)
}

group = "com.app"
version = "1.0.0-SNAPSHOT"

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(25)) }
    withSourcesJar()
}

dependencies {
    api(platform(libs.sb.bom))
    api(platform(libs.sc.bom))
    api(platform(libs.jackson.bom))
    api(libs.sb.autoconfigure)
    api(libs.jackson.annotations)
    api(libs.bundles.testbundle)
    api(libs.bundles.testcontainers)
    implementation(libs.jackson.databind)
    implementation(libs.sb.starter.json)
    compileOnly(libs.sb.starter.web)
    compileOnly(libs.sb.starter.webflux)
    compileOnly(libs.sb.starter.data.jpa)
    compileOnly(libs.spring.data.commons)
    compileOnly(libs.sb.starter.data.redis)
    compileOnly(libs.springdoc.openapi.webmvc)
    api(libs.bundles.tracing)
    api(libs.bouncycastle.bcprov)
    api(libs.mapstruct)
    compileOnly(libs.lombok)
    annotationProcessor(platform(libs.sb.bom))
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.mapstruct.processor)
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

spotless { java { googleJavaFormat("1.27.0") } }


tasks.bootJar { enabled = false }
tasks.jar { enabled = true }

tasks.build { dependsOn("publishToMavenLocal") }
