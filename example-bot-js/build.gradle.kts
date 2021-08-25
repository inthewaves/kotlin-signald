plugins {
    kotlin("js")
}

version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter() {
        content {
            includeModule("org.jetbrains.kotlinx", "kotlinx-nodejs")
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":client"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js")
    implementation("org.jetbrains.kotlinx:kotlinx-nodejs:0.0.7")
}

kotlin {
    js(IR) {
        binaries.executable()
        nodejs {

        }
        useCommonJs()
    }
}
