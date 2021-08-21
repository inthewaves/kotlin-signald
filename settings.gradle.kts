rootProject.name = "kotlin-signald"
include("clientprotocol")
include("client")

includeBuild("protocolgen-plugin")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}
