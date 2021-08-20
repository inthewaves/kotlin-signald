rootProject.name = "kotlin-signald"
include("clientprotocol")
includeBuild("protocolgen-plugin")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}
