package org.inthewaves.kotlinsignald.protocolgen

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

abstract class ProtocolGenConfig {
    var packageName: String = ""
    /**
     * A path to the output directory relative to the project dir.
     */
    var outputDirectory: String = ""
    /**
     * A JSON file of the signald client protocol. This can be path relative to the project or an absolute path.
     */
    var protocolJsonFile: String = ""

    companion object {
        internal const val EXTENSION_NAME = "signaldProtocolGen"
    }
}

@Suppress("unused")
class ProtocolGenPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create(
            ProtocolGenConfig.EXTENSION_NAME,
            ProtocolGenConfig::class.java
        )

        project.tasks.register("generateSignaldClasses") { task ->
            task.group = "build"
            task.description = "Generates Kotlin signald client protocol classes"
            task.doLast {
                val outputDirFile = File(project.projectDir, project.relativePath(extension.outputDirectory))
                val protocolJsonFile = File(project.projectDir, project.relativePath(extension.protocolJsonFile))
                    .takeUnless { !it.exists() || !it.canRead() }
                    ?: File(extension.protocolJsonFile)
                val packageName = extension.packageName
                if (packageName.isBlank()) {
                    error("Missing package name in ${ProtocolGenConfig.EXTENSION_NAME} configuration")
                }

                ProtocolGenerator(protocolJsonFile, outputDirFile, PackageName(packageName)).generateSignaldProtocol()
            }
        }
    }
}
