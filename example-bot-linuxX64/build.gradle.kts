plugins {
    kotlin("multiplatform")
}

version = "0.1.0-SNAPSHOT"

kotlin {
    linuxX64 {
        binaries {
            executable {
                entryPoint = "main"
            }
        }
    }

    sourceSets {
        val linuxX64Main by getting {
            dependencies {
                implementation(project(":client"))
            }
        }
    }
}