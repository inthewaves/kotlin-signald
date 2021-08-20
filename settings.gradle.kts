rootProject.name = "kotlin-signald"
include("lib")
includeBuild("protocolgen-plugin")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}
