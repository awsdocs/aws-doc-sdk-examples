import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "me.scmacdon"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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
}
apply(plugin = "org.jlleitschuh.gradle.ktlint")
dependencies {
    implementation("aws.sdk.kotlin:rdsdata:1.0.0")
    implementation("aws.sdk.kotlin:ses:1.0.0")
    implementation("aws.smithy.kotlin:http-client-engine-okhttp:0.30.0")
    implementation("aws.smithy.kotlin:http-client-engine-crt:0.30.0")
    implementation("org.springframework.boot:spring-boot-starter-web:2.7.5")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("javax.mail:javax.mail-api:1.6.2")
    implementation("com.sun.mail:javax.mail:1.6.2")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.7.5")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
