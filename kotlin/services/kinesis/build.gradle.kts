import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    application
}
group = "me.scmacdon"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("aws.sdk.kotlin:kinesis:0.14.3-beta")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.3")


}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
