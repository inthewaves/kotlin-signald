rootProject.name = "kotlin-signald"
include("client")
include("client-coroutines")
include("example-bot-jvm")
include("example-bot-linuxX64")
include("example-bot-js")

includeBuild("protocolgen-plugin")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

