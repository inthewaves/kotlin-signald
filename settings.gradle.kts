rootProject.name = "kotlin-signald"
include("client")
include("client-coroutines")
include("example-bot-jvm")

includeBuild("protocolgen-plugin")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}
