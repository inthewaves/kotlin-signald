plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "7.1.0"
    application
}

version = "0.1.0-SNAPSHOT"

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":client-coroutines"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
}

application {
    mainClass.set("org.inthewaves.examplejvmbot.ExampleBotMainKt")
}
