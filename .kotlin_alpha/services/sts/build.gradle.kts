import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    application
}
group = "me.scmacdon"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    api("aws.sdk.kotlin:sts:0.4.0-alpha")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")
}

configurations {
    all {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}