plugins {
    kotlin("jvm")
    id("com.github.johnrengelman.shadow") version "7.1.0"
    application
}

version = "0.1.0-SNAPSHOT"

repositories {
    mavenLocal {
        content {
            includeGroup("org.inthewaves.kotlin-signald")
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    // FIXME: shadowjar fails after removing the `java-library` plugin, but works fine through local Maven repo
    // Could not determine the dependencies of task ':example-bot-jvm:shadowJar'.
    // > Could not resolve all dependencies for configuration ':example-bot-jvm:runtimeClasspath'.
    //    > Could not resolve project :client.
    //      Required by:
    //          project :example-bot-jvm > project :client-coroutines
    //       > The consumer was configured to find a runtime of a library compatible with Java 11, packaged as a jar, preferably optimized for standard JVMs, and its dependencies declared externally, as well as attribute 'org.jetbrains.kotlin.platform.type' with value 'jvm'. However we cannot choose between the following variants of project :client:
    //           - jvmRuntimeElements
    //           - ktlint
    //         All of them match the consumer attributes:
    //           - Variant 'jvmRuntimeElements' capability org.inthewaves.kotlin-signald:client:0.17.0+signald-0.15.0-31-7354fec1 declares a runtime of a library, packaged as a jar, preferably optimized for standard JVMs, as well as attribute 'org.jetbrains.kotlin.platform.type' with value 'jvm':
    //               - Unmatched attributes:
    //                   - Doesn't say anything about how its dependencies are found (required its dependencies declared externally)
    //                   - Doesn't say anything about its target Java version (required compatibility with Java 11)
    //           - Variant 'ktlint' capability org.inthewaves.kotlin-signald:client:0.17.0+signald-0.15.0-31-7354fec1 declares a component, and its dependencies declared externally:
    //               - Unmatched attributes:
    //                   - Doesn't say anything about its component category (required a library)
    //                   - Doesn't say anything about its target Java environment (preferred optimized for standard JVMs)
    //                   - Doesn't say anything about its target Java version (required compatibility with Java 11)
    //                   - Doesn't say anything about its elements (required them packaged as a jar)
    //                   - Doesn't say anything about its usage (required a runtime)
    //                   - Doesn't say anything about org.jetbrains.kotlin.platform.type (required 'jvm')
    //         The following variants were also considered but didn't match the requested attributes:
    //           - Variant 'commonMainMetadataElements' capability org.inthewaves.kotlin-signald:client:0.17.0+signald-0.15.0-31-7354fec1 declares a library, preferably optimized for non-jvm:
    //               - Incompatible because this component declares a usage of 'kotlin-api' of a component, as well as attribute 'org.jetbrains.kotlin.platform.type' with value 'common' and the consumer needed a runtime of a component, as well as attribute 'org.jetbrains.kotlin.platform.type' with value 'jvm'
    //               - Other compatible attributes:
    //                   - Doesn't say anything about how its dependencies are found (required its dependencies declared externally)
    //                   - Doesn't say anything about its target Java version (required compatibility with Java 11)
    //                   - Doesn't say anything about its elements (required them packaged as a jar)
    //           - Variant 'jsApiElements' capability org.inthewaves.kotlin-signald:client:0.17.0+signald-0.15.0-31-7354fec1 declares a library, preferably optimized for non-jvm:
    //               - Incompatible because this component declares a usage of 'kotlin-api' of a component, as well as attribute 'org.jetbrains.kotlin.platform.type' with value 'js' and the consumer needed a runtime of a component, as well as attribute 'org.jetbrains.kotlin.platform.type' with value 'jvm'
    //               - Other compatible attributes:
    //                   - Doesn't say anything about how its dependencies are found (required its dependencies declared externally)
    //                   - Doesn't say anything about its target Java version (required compatibility with Java 11)
    //                   - Doesn't say anything about its elements (required them packaged as a jar)
    //           - Variant 'jsRuntimeElements' capability org.inthewaves.kotlin-signald:client:0.17.0+signald-0.15.0-31-7354fec1 declares a library, preferably optimized for non-jvm:
    //               - Incompatible because this component declares a usage of 'kotlin-runtime' of a component, as well as attribute 'org.jetbrains.kotlin.platform.type' with value 'js' and the consumer needed a runtime of a component, as well as attribute 'org.jetbrains.kotlin.platform.type' with value 'jvm'
    //               - Other compatible attributes:
    //                   - Doesn't say anything about how its dependencies are found (required its dependencies declared externally)
    //                   - Doesn't say anything about its target Java version (required compatibility with Java 11)
    //                   - Doesn't say anything about its elements (required them packaged as a jar)
    //           - Variant 'jvmApiElements' capability org.inthewaves.kotlin-signald:client:0.17.0+signald-0.15.0-31-7354fec1 declares a library, packaged as a jar, preferably optimized for standard JVMs, as well as attribute 'org.jetbrains.kotlin.platform.type' with value 'jvm':
    //               - Incompatible because this component declares an API of a component and the consumer needed a runtime of a component
    //               - Other compatible attributes:
    //                   - Doesn't say anything about how its dependencies are found (required its dependencies declared externally)
    //                   - Doesn't say anything about its target Java version (required compatibility with Java 11)
    //           - Variant 'linuxX64ApiElements' capability org.inthewaves.kotlin-signald:client:0.17.0+signald-0.15.0-31-7354fec1 declares a library, preferably optimized for non-jvm:
    //               - Incompatible because this component declares a usage of 'kotlin-api' of a component, as well as attribute 'org.jetbrains.kotlin.platform.type' with value 'native' and the consumer needed a runtime of a component, as well as attribute 'org.jetbrains.kotlin.platform.type' with value 'jvm'
    //               - Other compatible attributes:
    //                   - Doesn't say anything about how its dependencies are found (required its dependencies declared externally)
    //                   - Doesn't say anything about its target Java version (required compatibility with Java 11)
    //                   - Doesn't say anything about its elements (required them packaged as a jar)
    //           - Variant 'linuxX64CInteropApiElements' capability org.inthewaves.kotlin-signald:client:0.17.0+signald-0.15.0-31-7354fec1 declares a library, preferably optimized for non-jvm:
    //               - Incompatible because this component declares a usage of 'kotlin-cinterop' of a component, with the library elements 'cinterop-klib', as well as attribute 'org.jetbrains.kotlin.platform.type' with value 'native' and the consumer needed a runtime of a component, packaged as a jar, as well as attribute 'org.jetbrains.kotlin.platform.type' with value 'jvm'
    //               - Other compatible attributes:
    //                   - Doesn't say anything about how its dependencies are found (required its dependencies declared externally)
    //                   - Doesn't say anything about its target Java version (required compatibility with Java 11)
    //           - Variant 'linuxX64MetadataElements' capability org.inthewaves.kotlin-signald:client:0.17.0+signald-0.15.0-31-7354fec1 declares a library, preferably optimized for non-jvm:
    //               - Incompatible because this component declares a usage of 'kotlin-metadata' of a component, as well as attribute 'org.jetbrains.kotlin.platform.type' with value 'native' and the consumer needed a runtime of a component, as well as attribute 'org.jetbrains.kotlin.platform.type' with value 'jvm'
    //               - Other compatible attributes:
    //                   - Doesn't say anything about how its dependencies are found (required its dependencies declared externally)
    //                   - Doesn't say anything about its target Java version (required compatibility with Java 11)
    //                   - Doesn't say anything about its elements (required them packaged as a jar)
    //           - Variant 'metadataApiElements' capability org.inthewaves.kotlin-signald:client:0.17.0+signald-0.15.0-31-7354fec1 declares a library, preferably optimized for non-jvm:
    //               - Incompatible because this component declares a usage of 'kotlin-metadata' of a component, as well as attribute 'org.jetbrains.kotlin.platform.type' with value 'common' and the consumer needed a runtime of a component, as well as attribute 'org.jetbrains.kotlin.platform.type' with value 'jvm'
    //               - Other compatible attributes:
    //                   - Doesn't say anything about how its dependencies are found (required its dependencies declared externally)
    //                   - Doesn't say anything about its target Java version (required compatibility with Java 11)
    //                   - Doesn't say anything about its elements (required them packaged as a jar)
    // implementation(project(":client-coroutines"))
    implementation("org.inthewaves.kotlin-signald:client-coroutines:${project.parent!!.version}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>() {
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
}

application {
    mainClass.set("org.inthewaves.examplejvmbot.ExampleBotMainKt")
}
