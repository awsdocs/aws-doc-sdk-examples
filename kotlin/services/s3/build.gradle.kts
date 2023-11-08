import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    application
}

group = "me.scmacdon"
version = "1.0-SNAPSHOT"

buildscript {
    repositories {
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("org.jlleitschuh.gradle:ktlint-gradle:10.3.0")
    }
}

repositories {
    mavenCentral()
    jcenter()
}

apply(plugin = "org.jlleitschuh.gradle.ktlint")

dependencies {
    implementation("aws.sdk.kotlin:s3-jvm:0.26.0-beta")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("aws.smithy.kotlin:aws-signing-crt:0.21.0")
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.test {
    useJUnitPlatform()
}
