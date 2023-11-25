import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    java
    application
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"
    // alias(libs.plugins.detekt)
    alias(libs.plugins.javafx)
    alias(libs.plugins.badassruntime)
}

group = "com.marvinelsen"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://www.jitpack.io")
    }
}

dependencies {
    implementation(libs.kaml)
    implementation(libs.json)
    implementation(libs.slf4j.api)
    implementation(libs.slf4j.nop)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.sqlite)
    implementation(libs.ikonli.javafx)
    implementation(libs.ikonli.material2)
    implementation(libs.apache.commons.csv)
    implementation(libs.jieba)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.java)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.framework.datatest)
    testImplementation(libs.kotest.assertions.core)
}

application {
    mainClass.set("com.marvinelsen.willow.LauncherKt")
}

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

javafx {
    version = "21.0.1"
    modules("javafx.controls", "javafx.fxml", "javafx.graphics", "javafx.web")
}

tasks.test {
    useJUnitPlatform()
}

runtime {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
}

//detekt {
//    buildUponDefaultConfig = true
//    allRules = true
//}