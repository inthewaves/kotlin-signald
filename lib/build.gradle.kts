import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.jetbrains.dokka") version "1.5.0"
    id("org.inthewaves.kotlin-signald-protocolgen")
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.7.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    `maven-publish`
    `java-library`
    base
    signing
}

val mavenArtifactId = "kotlin-signald"
group = "org.inthewaves.kotlin-signald"
version = "0.1.0"

base {
    archivesName.set(mavenArtifactId)
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

val kotlinSourcesJar = tasks.findByName("kotlinSourcesJar")

publishing {
    publications {
        create<MavenPublication>("mavenKotlinSignald") {
            from(components["kotlin"])

            artifactId = mavenArtifactId
            artifact(kotlinSourcesJar)
            artifact(javadocJar)

            pom {
                name.set(artifactId)
                description.set("A Kotlin library for communicating with signald")
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
    dependsOn(tasks.test)
}

tasks.withType<PublishToMavenLocal> {
    dependsOn(tasks.test)
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}

signaldProtocolGen {
    packageName = "org.inthewaves.kotlinsignald"
    outputDirectory = "src/gen/kotlin"
    protocolJsonFile = "protocol.json"
}

kotlin {
    explicitApi()

    sourceSets {
        main {
            kotlin.setSrcDirs(mutableListOf("src/main/kotlin", "src/gen/kotlin"))
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.5.21"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.21")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")

    implementation("com.kohlschutter.junixsocket:junixsocket-common:2.3.2")
    implementation("com.kohlschutter.junixsocket:junixsocket-native-common:2.3.2")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.5.21")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    testImplementation("io.mockk:mockk:1.12.0")
}
