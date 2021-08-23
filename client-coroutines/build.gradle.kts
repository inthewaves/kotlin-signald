plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
    `maven-publish`
    `java-library`
    signing
}

description = "Coroutine-based message subscriptions"

kotlin {
    explicitApi()

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js(IR) {
        nodejs {}
        binaries.library()
    }

    val nativeBaseName = "kotlinsignald"

    linuxX64 {
        binaries {
            sharedLib {
                baseName = nativeBaseName
            }
        }
    }

    macosX64 {
        binaries {
            sharedLib {
                baseName = nativeBaseName
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":client"))
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}