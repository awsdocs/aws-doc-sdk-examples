import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    id("io.gitlab.arturbosch.detekt").version("1.16.0-RC1")
    application
}
group = "me.scmacdon"
version = "1.0-SNAPSHOT"

buildscript {
    repositories {
        jcenter()
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    jcenter()
}

dependencies {
    api("aws.sdk.kotlin:ses:0.4.0-alpha")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")
    implementation ("javax.mail:javax.mail-api:1.5.5")
    implementation ("com.sun.mail:javax.mail:1.5.5")
    implementation ("javax.activation:activation:1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")

}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}