package org.inthewaves.kotlinsignald

import org.newsclub.net.unix.AFUNIXSocketAddress
import java.io.IOException
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.Paths

@Throws(SocketUnavailableException::class)
internal fun getSocketAddressOrThrow(customPath: String?): AFUNIXSocketAddress {
    val socketPathsToTry = if (customPath != null) sequenceOf(customPath) else getDefaultSocketPaths()

    return socketPathsToTry
        .map {
            try {
                Paths.get(it)
            } catch (e: InvalidPathException) {
                null
            }
        }
        .filterNotNull()
        .filter {
            try {
                Files.exists(it) && Files.isWritable(it)
            } catch (e: SecurityException) {
                false
            }
        }
        .map { path ->
            try {
                AFUNIXSocketAddress.of(path)
            } catch (e: IOException) {
                null
            }
        }
        .filterNotNull()
        .firstOrNull()
        ?: throw SocketUnavailableException(
            if (customPath != null) {
                "unable to connect to signald socket at $customPath (${buildErrorMessageForPath(customPath)})"
            } else {
                "unable to connect to default signald socket paths " +
                    "(${getDefaultSocketPaths().map(::buildErrorMessageForPath).joinToString(";")})"
            }
        )
}

private fun buildErrorMessageForPath(path: String): String {
    val nioPath = Paths.get(path)
    return "$path: exists: ${Files.exists(nioPath)}, " +
        "readable ${Files.isReadable(nioPath)}, " +
        "writable ${Files.isWritable(nioPath)}"
}

internal actual fun getEnvVariable(envVarName: String): String? = System.getenv(envVarName)
