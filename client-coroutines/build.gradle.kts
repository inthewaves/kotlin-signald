plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
    `maven-publish`
    signing
}

description = "Coroutine-based message subscriptions"

repositories {
    mavenCentral()
    jcenter {
        content {
            includeModule("org.jetbrains.kotlinx", "kotlinx-nodejs")
        }
    }
}

kotlin {
    explicitApi()

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    js(IR) {
        nodejs {}
        binaries.library()
    }

    /*
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
     */

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":client"))
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}