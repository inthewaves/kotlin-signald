rootProject.name = "kotlin-signald"
include("client")

includeBuild("protocolgen-plugin")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}
