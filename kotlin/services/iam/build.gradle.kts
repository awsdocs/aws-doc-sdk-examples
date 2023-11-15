import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.0"
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
}
apply(plugin = "org.jlleitschuh.gradle.ktlint")
dependencies {
    implementation("aws.sdk.kotlin:iam:0.33.1-beta")
    implementation("aws.sdk.kotlin:sts:0.33.1-beta")
    implementation("aws.sdk.kotlin:s3:0.33.1-beta")
    implementation("aws.sdk.kotlin:secretsmanager:0.33.1-beta")
    implementation("aws.smithy.kotlin:http-client-engine-okhttp:0.28.0")
    implementation("aws.smithy.kotlin:http-client-engine-crt:0.28.0")
    implementation("com.google.code.gson:gson:2.10")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "17"
}
tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }

    // Define the test source set
    testClassesDirs += files("build/classes/kotlin/test")
    classpath += files("build/classes/kotlin/main", "build/resources/main")
}