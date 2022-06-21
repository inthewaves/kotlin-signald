plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
    id("kotlinx-atomicfu")
    id("org.jetbrains.dokka")
    id("org.jlleitschuh.gradle.ktlint")
    `maven-publish`
    signing
}

description = "A Kotlin Multiplatform client API to communicate with signald"

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompileCommon>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
    }
}

repositories {
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
        useCommonJs()
    }

    val nativeBaseName = "kotlinsignald"
    linuxX64 {
        binaries {
            sharedLib {
                baseName = nativeBaseName
                export(project(":clientprotocol"))
            }
        }
    }

    /*
    macosX64 {
        binaries {
            sharedLib {
                baseName = nativeBaseName
            }
        }
    }
     */

    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }

        val commonMain by getting {
            dependencies {
                api(project(":clientprotocol"))
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val synchronousClientMain by creating {
            dependsOn(commonMain)
        }
        val jvmMain by getting {
            dependsOn(synchronousClientMain)
            dependencies {
                implementation("com.kohlschutter.junixsocket:junixsocket-common:2.4.0")
                implementation("com.kohlschutter.junixsocket:junixsocket-native-common:2.4.0")
            }
        }
        val linuxX64Main by getting {
            dependsOn(synchronousClientMain)
        }

        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-nodejs:0.0.7")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.6.3")
            }
        }
    }
}
