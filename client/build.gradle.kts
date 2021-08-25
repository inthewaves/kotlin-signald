plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
    id("kotlinx-atomicfu")
    id("org.jetbrains.dokka")
    id("org.inthewaves.kotlin-signald-protocolgen")
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    `maven-publish`
    `java-library`
    signing
}

description = "A Kotlin Multiplatform library for communicating with signald"

signaldProtocolGen {
    packageName = "org.inthewaves.kotlinsignald"
    outputDirectory = "src/commonMain/generated"
    protocolJsonFile = "protocol.json"
}

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
        val commonMain by getting {
            kotlin.setSrcDirs(listOf("src/commonMain/kotlin", "src/commonMain/generated"))
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
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
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.5.1")
            }
        }
    }
}
