package org.inthewaves.kotlinsignald

import kotlinx.serialization.SerializationException
import org.inthewaves.kotlinsignald.clientprotocol.SignaldException
import org.inthewaves.kotlinsignald.clientprotocol.SignaldJson
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.Version
import org.newsclub.net.unix.AFUNIXSocket
import org.newsclub.net.unix.AFUNIXSocketAddress
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.nio.file.Paths

private fun isPathReadableAndWritable(path: Path) =
    Files.exists(path) && Files.isReadable(path) && Files.isWritable(path)

private fun getSocketAddressOrThrow(customPath: String?): AFUNIXSocketAddress {
    val socketPathToUse: Path = try {
        if (customPath != null) {
            customPath
                .let { Path.of(it) }
                .takeIf(::isPathReadableAndWritable)
        } else {
            Paths.get(System.getenv("XDG_RUNTIME_DIR"), "signald/signald.sock")
                .takeIf(::isPathReadableAndWritable)
                ?: Paths.get("/var/run/signald/signald.sock")
                    .takeIf(::isPathReadableAndWritable)
        }
    } catch (e: SecurityException) {
        throw SocketUnavailableException("failed to test socket due to SecurityException", e)
    } catch (e: InvalidPathException) {
        throw SocketUnavailableException("bad socket path ${e.input} (${e.reason})", e)
    } ?: throw SocketUnavailableException(
        if (customPath != null) {
            "tried $customPath but it doesn't exist"
        } else {
            "tried default paths but they don't exist"
        }
    )
    return AFUNIXSocketAddress(socketPathToUse.toFile())
}

private fun decodeVersionOrNull(versionLine: String?) = if (versionLine != null) {
    try {
        SignaldJson.decodeFromString(JsonMessageWrapper.serializer(Version.serializer()), versionLine).data
    } catch (e: SerializationException) {
        null
    }
} else {
    null
}

/**
 * A wrapper for a socket that creates a new socket connection for every request.
 */
internal actual class SocketWrapper @Throws(SocketUnavailableException::class) actual constructor(
    socketPath: String?
) : SocketCommunicator {
    private val socketAddress: AFUNIXSocketAddress = getSocketAddressOrThrow(socketPath)

    val version: Version? = useSocket(skipVersion = false) { _, reader, _ -> decodeVersionOrNull(reader.readLine()) }

    override fun submit(request: String): String {
        useSocket { _, reader, writer ->
            writer.println(request)
            return reader.readLine() ?: throw SignaldException("end of socket stream has been reached")
        }
    }

    override fun readLine(): String? {
        throw UnsupportedOperationException("this implementation only supports a single socket")
    }

    private inline fun <T> useSocket(
        skipVersion: Boolean = true,
        socketBlock: (socket: AFUNIXSocket, reader: BufferedReader, writer: PrintWriter) -> T
    ): T {
        val socket: AFUNIXSocket = try {
            AFUNIXSocket.connectTo(socketAddress)
        } catch (e: IOException) {
            throw SocketUnavailableException("failed to connect to socket", e)
        }
        val reader = BufferedReader(InputStreamReader(socket.inputStream))
        val writer = PrintWriter(socket.outputStream, true)
        if (skipVersion) {
            reader.readLine()
        }
        return socket.use { socketBlock(it, reader, writer) }
    }
}

/**
 * A wrapper for a socket that maintains a socket connection for every request, ideal for receiving chat messages
 * from a subscribe request.
 */
internal actual class PersistentSocketWrapper @Throws(SocketUnavailableException::class) actual constructor(
    socketPath: String?
) : SocketCommunicator, AutoCloseable {
    private val socket: AFUNIXSocket = AFUNIXSocket.connectTo(getSocketAddressOrThrow(socketPath))
    private val writer: PrintWriter = PrintWriter(socket.outputStream, true)
    private val reader: BufferedReader = socket.inputStream.bufferedReader()
    val version: Version?

    init {
        val versionLine = reader.readLine()
        version = decodeVersionOrNull(versionLine)
    }

    override fun submit(request: String): String {
        writer.println(request)
        return reader.readLine() ?: throw SignaldException("end of socket stream has been reached")
    }

    override fun readLine(): String? = reader.readLine()

    actual override fun close() {
        socket.close()
    }
}
