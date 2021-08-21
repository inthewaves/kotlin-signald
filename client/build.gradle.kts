plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    `maven-publish`
    `java-library`
    signing
}

version = "0.4.0"
description = "A Kotlin Multiplatform library for communicating with signald"

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
                api(project(":clientprotocol"))
            }
        }
        val commonTest by getting {
            dependencies {
               implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("com.kohlschutter.junixsocket:junixsocket-common:2.3.2")
                implementation("com.kohlschutter.junixsocket:junixsocket-native-common:2.3.2")
            }
        }
    }
}