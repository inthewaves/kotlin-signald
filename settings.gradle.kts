rootProject.name = "kotlin-signald"
include("client")
include("client-coroutines")

includeBuild("protocolgen-plugin")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}
