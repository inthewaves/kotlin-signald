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
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    /*

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
     */

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val synchronousClientMain by creating {
            dependsOn(commonMain)
            dependencies {
                api(project(":client"))
            }
        }
        val jvmMain by getting {
            dependsOn(synchronousClientMain)
        }
    }
}