import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.serialization") version "1.9.20"
    // alias(libs.plugins.detekt)
    alias(libs.plugins.javafx)
}

group = "com.marvinelsen"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.marvinelsen.willowkotlin.HelloApplication")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
}

javafx {
    version = "21.0.1"
    modules("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation(libs.kaml)
    implementation(libs.slf4j.api)
    implementation(libs.slf4j.nop)

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.framework.datatest)
    testImplementation(libs.kotest.assertions.core)
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

//detekt {
//    buildUponDefaultConfig = true
//    allRules = true
//}