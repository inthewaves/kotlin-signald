plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("java-gradle-plugin")
}

group = "org.inthewaves"
version = "0.1.0"

gradlePlugin {
    plugins {
        create("kotlin-signald-protocolgen") {
            id = "org.inthewaves.kotlin-signald-protocolgen"
            implementationClass = "org.inthewaves.kotlinsignald.protocolgen.ProtocolGenPlugin"
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup:kotlinpoet:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.1")
}
