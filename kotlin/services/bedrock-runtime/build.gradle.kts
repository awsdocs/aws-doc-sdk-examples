plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    application
}

group = "com.example.bedrockruntime"
version = "1.0-SNAPSHOT"

val awsSdkVersion = "1.4.27"
val junitVersion = "5.12.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("aws.sdk.kotlin:bedrockruntime:$awsSdkVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.8.0")

    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    testImplementation("org.jetbrains.kotlin:kotlin-reflect")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

application {
    mainClass.set("com.example.bedrockruntime.InvokeModelKt")
}