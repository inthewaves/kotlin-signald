package org.inthewaves.kotlinsignald

import org.inthewaves.kotlinsignald.clientprotocol.SignaldException
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonVersionMessage
import org.newsclub.net.unix.AFUNIXSocket
import org.newsclub.net.unix.AFUNIXSocketAddress
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter

/**
 * A wrapper for a socket that makes new socket connections for every request and closes the connection after a request,
 * making it thread safe.
 *
 * @param socketPath An optional path to the signald socket. If this is null, it will attempt the default socket
 * locations (`$XDG_RUNTIME_DIR/signald/signald.sock` and `/var/run/signald/signald.sock`)
 * @throws SocketUnavailableException if unable to connect to the socket
 * @throws IOException if there is any I/O errors when communicating to the socket (it first returns the signald version
 * JSON as [Version]).
 */
public actual class SocketWrapper @Throws(SocketUnavailableException::class) private constructor(
    socketPath: String?
) : SocketCommunicator {
    private val socketAddress: AFUNIXSocketAddress = getSocketAddressOrThrow(socketPath)

    /**
     * Version of signald. This is sent when we connect to the socket.
     */
    public val version: JsonVersionMessage? = useNewSocketConnection(skipVersion = false) { reader, _ ->
        decodeVersionOrNull(reader.readLine())
    }

    public actual val actualSocketPath: String
        get() = socketAddress.path

    override fun submit(request: String): String {
        useNewSocketConnection { reader, writer ->
            writer.println(request)
            return reader.readLine() ?: throw SignaldException("end of socket stream has been reached")
        }
    }

    override fun readLine(): String? {
        throw UnsupportedOperationException("this implementation only supports a single socket")
    }

    actual override fun close() {}

    @Throws(IOException::class)
    private inline fun <T> useNewSocketConnection(
        skipVersion: Boolean = true,
        socketBlock: (reader: BufferedReader, writer: PrintWriter) -> T
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
        return socket.use { socketBlock(reader, writer) }
    }

    public actual companion object {
        /**
         * @param socketPath An optional path to the signald socket. If this is null, it will attempt the default socket
         * locations (`$XDG_RUNTIME_DIR/signald/signald.sock` and `/var/run/signald/signald.sock`)
         * @throws SocketUnavailableException if unable to connect to the socket
         * @throws IOException if there is any I/O errors when communicating to the socket (signald first returns the
         * signald version JSON as [JsonVersionMessage]).
         */
        @Throws(IOException::class)
        public actual fun create(socketPath: String?): SocketWrapper = SocketWrapper(socketPath)
    }
}

/**
 * A wrapper for a socket that maintains a socket connection for every request, ideal for receiving chat messages
 * after a subscribe request.
 *
 * @param socketPath An optional path to the signald socket. If this is null, it will attempt the default socket
 * locations (`$XDG_RUNTIME_DIR/signald/signald.sock` and `/var/run/signald/signald.sock`)
 */
public actual class PersistentSocketWrapper private constructor(
    socketPath: String?
) : SocketCommunicator, AutoCloseable {
    private val socket: AFUNIXSocket = AFUNIXSocket.connectTo(getSocketAddressOrThrow(socketPath))
    private val writer: PrintWriter = PrintWriter(socket.outputStream, true)
    private val reader: BufferedReader = socket.inputStream.bufferedReader()

    /**
     * Version of signald.
     */
    public val version: JsonVersionMessage? = decodeVersionOrNull(reader.readLine())

    override fun submit(request: String): String {
        writer.println(request)
        return reader.readLine() ?: throw SignaldException("end of socket stream has been reached")
    }

    override fun readLine(): String? = reader.readLine()

    public actual override fun close() {
        socket.close()
    }

    public actual companion object {
        /**
         * @param socketPath An optional path to the signald socket. If this is null, it will attempt the default socket
         * locations (`$XDG_RUNTIME_DIR/signald/signald.sock` and `/var/run/signald/signald.sock`)
         */
        @Throws(SocketUnavailableException::class)
        public actual fun create(socketPath: String?): PersistentSocketWrapper = PersistentSocketWrapper(socketPath)
    }
}
