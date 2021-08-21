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

internal actual class SocketWrapper @Throws(SocketUnavailableException::class) actual constructor(
    socketPath: String?
) : SocketCommunicator {
    private val socketAddress: AFUNIXSocketAddress

    val version: Version?

    init {
        val socketPathToUse: Path = try {
            socketPath
                ?.let { Path.of(it) }
                ?.takeIf { Files.exists(it) && Files.isReadable(it) && Files.isWritable(it) }
                ?: Paths.get(System.getenv("XDG_RUNTIME_DIR"), "signald/signald.sock")
                    .takeIf { Files.exists(it) && Files.isReadable(it) && Files.isWritable(it) }
                ?: Paths.get("/var/run/signald/signald.sock")
                    .takeIf { Files.exists(it) && Files.isReadable(it) && Files.isWritable(it) }
        } catch (e: SecurityException) {
            throw SocketUnavailableException("failed to test socket due to SecurityException", e)
        } catch (e: InvalidPathException) {
            throw SocketUnavailableException("bad socket path ${e.input} (${e.reason})", e)
        } ?: throw SocketUnavailableException(
            if (socketPath != null) {
                "tried $socketPath but it doesn't exist"
            } else {
                "tried default paths but they don't exist"
            }
        )
        socketAddress = AFUNIXSocketAddress(socketPathToUse.toFile())

        version = useSocket(skipVersion = false) { _, reader, _ ->
            val versionLine = reader.readLine()
            if (versionLine != null) {
                try {
                    SignaldJson.decodeFromString(JsonMessageWrapper.serializer(Version.serializer()), versionLine).data
                } catch (e: SerializationException) {
                    null
                }
            } else {
                null
            }
        }
    }

    override fun submit(request: String): String {
        useSocket { _, reader, writer ->
            writer.println(request)
            return reader.readLine() ?: throw SignaldException("end of socket stream has been reached")
        }
    }

    override fun readLine(): String = throw UnsupportedOperationException("single socket")

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
