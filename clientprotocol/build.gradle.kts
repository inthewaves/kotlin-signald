plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlinx.binary-compatibility-validator")
    id("kotlinx-atomicfu")
    id("org.jetbrains.dokka")
    id("org.inthewaves.kotlin-signald-protocolgen")
    id("org.jlleitschuh.gradle.ktlint")
    `maven-publish`
    signing
}

description = "A Kotlin Multiplatform library containing model classes communicating with signald"

signaldProtocolGen {
    packageName = "org.inthewaves.kotlinsignald"
    outputDirectory = "src/commonMain/generated"
    protocolJsonFile = "protocol.json"
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

    val nativeBaseName = "kotlinsignaldprotocol"
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
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }

        val commonMain by getting {
            kotlin.setSrcDirs(listOf("src/commonMain/kotlin", "src/commonMain/generated"))
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        val nonJvmMain by creating {
            dependsOn(commonMain)
        }
        val jsMain by getting {
            dependsOn(nonJvmMain)
        }
        val linuxX64Main by getting {
            dependsOn(nonJvmMain)
        }
        val macosX64Main by getting {
            dependsOn(nonJvmMain)
        }
    }
}
