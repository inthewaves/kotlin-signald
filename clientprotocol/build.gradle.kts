import org.jetbrains.dokka.gradle.DokkaTask

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
version = "0.2.0"

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
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
        binaries.library()
    }

    linuxX64 {
        binaries {
            sharedLib {
                baseName = "kotlinsignald"
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
        // create<MavenPublication>("mavenKotlinSignald") {
        //    from(components["kotlin"])
        // }
    }
}

afterEvaluate {
    publishing {
        publications.all {
            val variantName = project.name
            val type = name
            println("Configuring $type ($variantName)")
            when (type) {
                "kotlinMultiplatform" -> {
                    // https://github.com/Kotlin/kotlinx.serialization/blob/43d5f7841fc744b072a636b712e194081456b5ba/gradle/publishing.gradle#L84
                    // https://github.com/Kotlin/kotlinx.serialization/blob/43d5f7841fc744b072a636b712e194081456b5ba/gradle/publish-mpp-root-module-in-platform.gradle
                    val platformPublication = publications["jvm"] as MavenPublication
                    afterEvaluate {
                        lateinit var platformXml: XmlProvider
                        platformPublication.pom.withXml { platformXml = this }
                        (publishing.publications["kotlinMultiplatform"] as MavenPublication).pom.withXml {
                            val root: groovy.util.Node = asNode()
                            root.children().toList().forEach { root.remove(it as groovy.util.Node) }
                            platformXml.asNode().children().forEach { root.append(it as groovy.util.Node) }

                            // Adjust the self artifact ID, as it should match the root module's coordinates:
                            ((root.get("artifactId") as groovy.util.NodeList).first() as groovy.util.Node)
                                .setValue(mavenArtifactId)

                            // Set packaging to POM to indicate that there's no artifact:
                            root.appendNode("packaging", "pom")

                            // Remove the original platform dependencies and add a single dependency on the platform
                            // module:
                            val dependencies: groovy.util.Node = (root.get("dependencies") as groovy.util.NodeList)
                                .first() as groovy.util.Node
                            dependencies.children().toList().forEach { dependencies.remove(it as groovy.util.Node) }
                            val singleDependency = dependencies.appendNode("dependency")
                            singleDependency.appendNode("groupId", platformPublication.groupId)
                            singleDependency.appendNode("artifactId", platformPublication.artifactId)
                            singleDependency.appendNode("version", platformPublication.version)
                            singleDependency.appendNode("scope", "compile")
                        }
                    }
                }
            }
        }
    }
}

tasks.withType<PublishToMavenRepository> {
    dependsOn(tasks.test, tasks.getByName("apiCheck"))
}

tasks.withType<PublishToMavenLocal> {
    dependsOn(tasks.test, tasks.getByName("apiCheck"))
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}
