plugins {
    kotlin("jvm")
}

group = "project"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

val jooqVersion: String by extra { "3.19.5" }

dependencies {
    implementation("org.jooq:jooq-codegen:${jooqVersion}")
    runtimeOnly("org.postgresql:postgresql:42.7.3")
}