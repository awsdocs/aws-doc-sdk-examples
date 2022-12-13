import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.7.10"
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
	jcenter()
}
apply(plugin = "org.jlleitschuh.gradle.ktlint")
dependencies {
	implementation("aws.sdk.kotlin:sns:0.19.0-beta")
	testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
	implementation("com.fasterxml.jackson.core:jackson-core:2.14.0")
	implementation("com.fasterxml.jackson.core:jackson-databind:2.14.0")
}
tasks.withType<KotlinCompile>() {
	kotlinOptions.jvmTarget = "1.8"
}

