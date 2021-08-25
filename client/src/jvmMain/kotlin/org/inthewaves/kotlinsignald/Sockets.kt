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
                Files.exists(it) && Files.isReadable(it) && Files.isWritable(it)
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
                "unable to connect to signald socket at $customPath"
            } else {
                "unable to connect to default signald socket paths"
            }
        )
}

internal actual fun getEnvVariable(envVarName: String): String? = System.getenv(envVarName)
