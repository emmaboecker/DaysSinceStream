import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30"
    kotlin("plugin.serialization") version "1.5.30"
    application
}

group = "net.stckoverflw"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.cdimascio", "dotenv-kotlin", "6.2.2")

    implementation("io.ktor", "ktor-client-okhttp", "1.5.2")
    implementation("io.ktor", "ktor-client-serialization", "1.5.2")

    implementation("com.google.code.gson:gson:2.8.7")

    implementation("io.github.redouane59.twitter", "twittered", "2.6")

    implementation("org.jetbrains.kotlinx", "kotlinx-datetime", "0.1.1")

    implementation("ch.qos.logback", "logback-classic", "1.2.3")
    implementation("org.slf4j", "slf4j-simple", "1.7.30")

    implementation("dev.inmo", "krontab", "0.6.1")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
        }
    }
}

application {
    mainClass.set("net.stckoverflw.dss.LauncherKt")
}