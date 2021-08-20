package org.inthewaves.kotlinsignald.protocolgen

private fun isDashOrUnderscore(char: Char) = char == '_' || char == '-'

object GenUtil {
    fun getClientProtocolPackage(pkg: PackageName) = "$pkg.clientprotocol"

    fun getStructuresPackage(pkg: PackageName, version: SignaldProtocolVersion) =
        "${getClientProtocolPackage(pkg)}.${version.name}.structures"

    fun getRequestsPackage(pkg: PackageName, version: SignaldProtocolVersion) =
        "${getClientProtocolPackage(pkg)}.${version.name}.requests"

    @OptIn(ExperimentalStdlibApi::class)
    fun snakeDashCaseToCamelCase(string: String): String {
        val underscoreDashCount = string.count(::isDashOrUnderscore)
        if (underscoreDashCount == 0) {
            return string
        }

        return buildString(string.length - underscoreDashCount) {
            var charToRemoveEncountered = false
            for (char in string) {
                if (!isDashOrUnderscore(char)) {
                    if (charToRemoveEncountered) {
                        append(char.uppercaseChar())
                        charToRemoveEncountered = false
                    } else {
                        append(char)
                    }
                } else {
                    charToRemoveEncountered = true
                }
            }
        }.intern()
    }
}

val String.isSnakeDashCase: Boolean
    get() = (count(::isDashOrUnderscore)) != 0

@OptIn(ExperimentalStdlibApi::class)
fun String.capitalizeFirst(): String = this.replaceFirstChar { it.uppercaseChar() }

fun String.snakeDashToCamelCase(): String = GenUtil.snakeDashCaseToCamelCase(this)