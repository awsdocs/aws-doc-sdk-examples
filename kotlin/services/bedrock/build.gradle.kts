import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.0"
    application
}

group = "com.example.bedrock"
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
        classpath("org.jlleitschuh.gradle:ktlint-gradle:11.3.1")
    }
}

repositories {
    mavenCentral()
}
apply(plugin = "org.jlleitschuh.gradle.ktlint")
dependencies {
    implementation("aws.sdk.kotlin:bedrock:1.5.63")
    implementation("aws.sdk.kotlin:sts:1.5.63")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}
tasks.withType<KotlinCompile> {
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
