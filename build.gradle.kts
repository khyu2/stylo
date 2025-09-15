import org.jooq.meta.jaxb.Database
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Generate
import org.jooq.meta.jaxb.SchemaMappingType
import org.jooq.meta.jaxb.Strategy

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("dev.monosoul.jooq-docker") version "6.0.14" // jOOQ Docker plugin
    id("com.google.cloud.tools.jib") version "3.4.4" // Docker image build plugin
    id("co.uzzu.dotenv.gradle") version "2.0.0" // .env support
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
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    runtimeOnly("org.postgresql:postgresql")

    // flyway
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    // thymeleaf
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:3.2.1")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")

    // swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

    // minio
    implementation("io.minio:minio:8.5.17")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    // jooq
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.jooq:jooq:${jooqVersion}")
    jooqCodegen(project(":jooq"))
    jooqCodegen("org.jooq:jooq:${jooqVersion}")
    jooqCodegen("org.jooq:jooq-meta:${jooqVersion}")
    jooqCodegen("org.jooq:jooq-codegen:${jooqVersion}")
    jooqCodegen("org.flywaydb:flyway-core:11.2.0")
    jooqCodegen("org.flywaydb:flyway-database-postgresql:10.10.0")
    jooqCodegen("org.postgresql:postgresql:42.6.0")

    // cache
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine")

    // json
    implementation("com.googlecode.json-simple:json-simple:1.1.1")

    // user agent parser
    implementation("nl.basjes.parse.useragent:yauaa:6.12")

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

    // Testcontainers for tests
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:jdbc")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register("cleanGeneratedJooq") {
    doLast { delete("src/generated") }
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
    version = jooqVersion

    withContainer {
        image {
            name = "postgres"
            envVars = mapOf(
                "POSTGRES_USER" to "postgres",
                "POSTGRES_PASSWORD" to "1234",
                "POSTGRES_DB" to "stylo",
            )

            db {
                username = "postgres"
                password = "1234"
                name = "stylo"
                jdbc {
                    schema = "jdbc:postgresql"
                    driverClassName = "org.postgresql.Driver"
                }
            }
        }
    }
}

tasks {
    generateJooqClasses {
        schemas = listOf("public")
        outputDirectory = project.layout.projectDirectory.dir("src/generated")
        includeFlywayTable = false

        usingJavaConfig {
            name = "org.jooq.codegen.KotlinGenerator"

            generate = Generate()
                .withDaos(false)
                .withRecords(false)
                .withFluentSetters(true)
                .withJavaTimeTypes(true)
                .withDeprecated(false)

            withStrategy(Strategy().withName("jooq.custom.generator.JPrefixGeneratorStrategy"))

            withDatabase(
                Database()
                    .withName("org.jooq.meta.postgres.PostgresDatabase")

                    .withSchemata(
                        SchemaMappingType()
                            .withInputSchema("public")
                    )
                    .withForcedTypes(
                        ForcedType()
                            .withUserType("java.lang.Long")
                            .withIncludeExpression(".*\\.id"),
                        ForcedType()
                            .withUserType("java.lang.Long")
                            .withIncludeTypes("int4|integer|serial|bigserial")
                    )
            )
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

jib {
    from {
        image = "eclipse-temurin:17-jre"
        platforms {
            // platform {
            //     architecture = "amd64"
            //     os = "linux"
            // }
            platform {
                architecture = "arm64"
                os = "linux"
            }
        }
    }
    to {
        image = env.DOCKER_IMAGE.orElse("docker.io/${project.name}")
        tags = setOf(project.version.toString(), "latest")

        auth {
            username = env.DOCKER_USERNAME.orElse("")
            password = env.DOCKER_PASSWORD.orElse("")
        }
    }
    container {
        ports = listOf("8080")
        jvmFlags = listOf(
            "-Duser.timezone=Asia/Seoul",
            // "-Dspring.profiles.active=local",
        )
        creationTime = "USE_CURRENT_TIMESTAMP"
        setAllowInsecureRegistries(false)
    }
}