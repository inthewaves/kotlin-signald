import org.jetbrains.kotlin.util.capitalizeDecapitalize.toLowerCaseAsciiOnly
import java.net.URI

plugins {
    id("org.jetbrains.dokka") version "1.6.10"
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.8.0" apply false
    id("org.jlleitschuh.gradle.ktlint") version "10.2.1" apply false
    `maven-publish`
    signing
}

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.8.0")
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.17.1")
    }
}

allprojects {
    group = "org.inthewaves.kotlin-signald"
    version = "0.26.0+signald-0.17.0-13-e1a25462"

    repositories {
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven") }
    }
}

subprojects {
    afterEvaluate setupPublish@{
        if (name !in arrayOf("client", "clientprotocol", "client-coroutines")) {
            return@setupPublish
        }

        tasks.withType<AbstractPublishToMaven> {
            dependsOn(tasks.getByName("apiCheck"), tasks.getByName("allTests"))
        }

        val dokkaJavadocJar by tasks.register<Jar>("dokkaJavadocJar") {
            dependsOn(tasks.dokkaHtml)
            from(tasks.dokkaHtml.flatMap { it.outputDirectory })
            archiveClassifier.set("javadoc")
        }

        publishing {
            publications {
                repositories {
                    maven {
                        name = "sonatype"
                        url = if (project.version.toString().endsWith("SNAPSHOT")) {
                            URI("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                        } else {
                            URI("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                        }
                        credentials {
                            val sonatypeUsername: String? by project
                            val sonatypePassword: String? by project

                            username = sonatypeUsername
                            password = sonatypePassword
                        }
                    }
                }

                withType<MavenPublication> {
                    artifact(dokkaJavadocJar)
                    pom {
                        name.set(project.name)
                        description.set(project.description)
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
    }
}
