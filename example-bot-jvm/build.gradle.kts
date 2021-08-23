plugins {
    kotlin("multiplatform")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

version = "0.1.0-SNAPSHOT"

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }

    linuxX64 {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":client-coroutines"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
                implementation("com.autodesk:coroutineworker:0.7.1")
            }
        }
    }
}
