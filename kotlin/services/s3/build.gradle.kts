import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.0"
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
        classpath("org.jlleitschuh.gradle:ktlint-gradle:11.5.1")
    }
}

repositories {
    mavenCentral()
}
apply(plugin = "org.jlleitschuh.gradle.ktlint")

val kotlinSdkVersion = "1.4.119"
val smithyKotlinVersion = "1.4.22"
dependencies {
    // AWS SDK for Kotlin
    implementation("aws.sdk.kotlin:s3:$kotlinSdkVersion")
    implementation("aws.sdk.kotlin:s3control:$kotlinSdkVersion")
    implementation("aws.sdk.kotlin:sts:$kotlinSdkVersion")
    implementation("aws.sdk.kotlin:secretsmanager:$kotlinSdkVersion")

    // Smithy runtime (you can choose one HTTP engine, but both are listed for flexibility)
    implementation("aws.smithy.kotlin:http-client-engine-okhttp:$smithyKotlinVersion")
    implementation("aws.smithy.kotlin:http-client-engine-crt:$smithyKotlinVersion")
    implementation("aws.smithy.kotlin:aws-signing-crt:$smithyKotlinVersion")
    implementation("aws.smithy.kotlin:aws-signing-common-jvm:$smithyKotlinVersion")
    implementation("aws.smithy.kotlin:aws-signing-default-jvm:$smithyKotlinVersion")
    implementation("aws.smithy.kotlin:http-auth-aws:$smithyKotlinVersion")

    // Optional if you directly use OkHttp APIs (outside AWS SDK)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Logging: SLF4J API + Log4j 2 backend
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    implementation("org.apache.logging.log4j:log4j-core:2.20.0")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0")

    // Other libraries
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("com.google.code.gson:gson:2.10")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.2")

    // Testing
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.freeCompilerArgs += "-Xlint:-deprecation"
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
