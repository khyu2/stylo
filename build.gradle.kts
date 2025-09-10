import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.SchemaMappingType

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("nu.studer.jooq") version "9.0"
}

group = "project"
version = "0.0.1-SNAPSHOT"
description = "stylo"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

val jooqVersion by extra { "3.19.5" }

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    runtimeOnly("org.postgresql:postgresql")

    // thymeleaf
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:3.2.1")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")

    // swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

    // minio
    implementation("io.minio:minio:8.5.17")

    // jooq
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    jooqGenerator(project(":jooq"))
    implementation("org.jooq:jooq:${jooqVersion}")
    jooqGenerator("org.jooq:jooq:${jooqVersion}")
    jooqGenerator("org.jooq:jooq-meta:${jooqVersion}")
    jooqGenerator("org.postgresql:postgresql:42.7.3")

    // cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")

    // json
    implementation("com.googlecode.json-simple:json-simple:1.1.1")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // kotest
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")
    testImplementation("io.mockk:mockk:1.13.12")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// npm을 사용하여 Tailwind CSS 빌드
val npmInstall by tasks.register<Exec>("npmInstall") {
    workingDir = file("${project.projectDir}")
    commandLine("npm", "install")
}

val npmBuildCss by tasks.register<Exec>("npmBuildCss") {
    workingDir = file("${project.projectDir}")
    commandLine("npm", "run", "build:css")
    dependsOn(npmInstall)
}

tasks.named("processResources") {
    dependsOn(npmBuildCss)
}

tasks.named("bootJar") {
    dependsOn(npmBuildCss)
}

jooq {
    version.set(jooqVersion)

    configurations {
        create("main") {
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "org.postgresql.Driver"
                    url = "jdbc:postgresql://localhost:5555/stylo"
                    user = "postgres"
                    password = "root"
                }

                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        schemata.add(
                            SchemaMappingType().withInputSchema("public")
                        )
                        forcedTypes.addAll(
                            listOf(
                                ForcedType()
                                    .withUserType("java.lang.Long")
                                    .withIncludeExpression(".*\\.id"),
                                ForcedType()
                                    .withUserType("java.lang.Long")
                                    .withIncludeTypes("int4|integer|serial|bigserial")
                            )
                        )
                    }
                    generate.apply {
                        isDaos = false
                        isRecords = false
                        isFluentSetters = true
                        isJavaTimeTypes = true
                        isDeprecated = false
                    }
                    target.apply {
                        directory = "src/generated"
                    }
                    strategy.name = "jooq.custom.generator.JPrefixGeneratorStrategy"
                }
            }
        }
    }
}

sourceSets {
    main {
        java {
            setSrcDirs(listOf("src/main/kotlin", "src/generated"))
        }
    }
}