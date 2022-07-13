import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
}
group = "me.test"
version = "1.0-SNAPSHOT"

buildscript {
    repositories {
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("org.jlleitschuh.gradle:ktlint-gradle:10.2.1")
    }
}

repositories {
   mavenCentral()
}

apply(plugin = "org.jlleitschuh.gradle.ktlint")
dependencies {
    implementation("aws.sdk.kotlin:autoscaling:0.14.3-beta")
    implementation("com.pinterest:ktlint:0.34.2")
    implementation("aws.sdk.kotlin:sts:0.12.0-beta")
    implementation("aws.sdk.kotlin:s3:0.12.0-beta")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
