import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.20"

    application
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
   implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation ("javax.mail:javax.mail-api:1.5.5")
    implementation ("com.sun.mail:javax.mail:1.5.5")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")
    implementation("com.amazonaws:aws-lambda-java-core:1.2.0")
    implementation("com.google.code.gson:gson:2.8.4")
    api("aws.sdk.kotlin:dynamodb:0.4.0-SNAPSHOT")
    api("aws.sdk.kotlin:ses:0.4.0-SNAPSHOT")

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