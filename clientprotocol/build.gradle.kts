import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka") version "1.5.0"
    id("org.inthewaves.kotlin-signald-protocolgen")
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.7.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    `maven-publish`
    `java-library`
    signing
}

val mavenArtifactId = "clientprotocol"
group = "org.inthewaves.kotlin-signald"
version = "0.3.1-SNAPSHOT"

signaldProtocolGen {
    packageName = "org.inthewaves.kotlinsignald"
    outputDirectory = "src/commonMain/generated"
    protocolJsonFile = "protocol.json"
}

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
    }
}

val javadocJar: TaskProvider<Jar> = run {
    val dokkaOutputDir = "$buildDir/dokka"

    tasks.getByName<DokkaTask>("dokkaHtml") {
        outputDirectory.set(file(dokkaOutputDir))
    }

    val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") {
        delete(dokkaOutputDir)
    }

    tasks.register<Jar>("javadocJar") {
        dependsOn(deleteDokkaOutputDir, tasks.dokkaHtml)
        archiveClassifier.set("javadoc")
        from(dokkaOutputDir)
    }
}

publishing {
    publications {
        repositories {
            maven {
                name = "sonatype"
                url = if (version.toString().endsWith("SNAPSHOT")) {
                    uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                } else {
                    uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                }
            }
        }

        withType<MavenPublication> {
            // artifactId = mavenArtifactId
            // artifact(tasks.kotlinSourcesJar)
            artifact(javadocJar)

            pom {
                name.set(mavenArtifactId)
                description.set("signald request and response classes in Kotlin")
                url.set("https://github.com/inthewaves/kotlin-signald")

                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("inthewaves")
                        name.set("Paul Ngo")
                        email.set("inthewaves@inthewaves.org")
                        organizationUrl.set("https://github.com/inthewaves")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/inthewaves/kotlin-signald.git")
                    developerConnection.set("scm:git:ssh://github.com:inthewaves/kotlin-signald.git")
                    url.set("https://github.com/inthewaves/kotlin-signald/tree/main")
                }
            }
        }
    }
}

tasks.withType<PublishToMavenRepository> {
    dependsOn(tasks.getByName("allTests"), tasks.getByName("apiCheck"))
}

tasks.withType<PublishToMavenLocal> {
    dependsOn(tasks.getByName("allTests"), tasks.getByName("apiCheck"))
}

signing {
    if (System.getenv()["CI"]?.toLowerCaseAsciiOnly() == "true") {
        val signingKeyId: String? by project
        val signingKey: String? by project
        val signingPassword: String? by project
        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    } else {
        useGpgCmd()
    }
    sign(publishing.publications)
}
