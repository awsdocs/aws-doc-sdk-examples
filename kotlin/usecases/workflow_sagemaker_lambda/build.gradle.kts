import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.1.0"
    application
}

group = "me.scmacdon"
version = "1.0-SNAPSHOT"

// Add the main class information
application {
    mainClassName = "org.example.SageMakerLambdaFunction"
}

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
    gradlePluginPortal()
}

apply(plugin = "org.jlleitschuh.gradle.ktlint")
dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.amazonaws:aws-java-sdk-lambda:1.12.429")
    implementation("com.amazonaws:aws-lambda-java-core:1.2.2")
    implementation("com.amazonaws:aws-lambda-java-events:3.11.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("aws.sdk.kotlin:sagemaker-jvm:0.29.1-beta")
    implementation("aws.sdk.kotlin:sagemakergeospatial-jvm:0.29.1-beta")
    implementation("com.google.code.gson:gson:2.10")
    implementation("org.json:json:20230227")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
