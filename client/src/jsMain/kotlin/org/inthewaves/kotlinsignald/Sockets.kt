package org.inthewaves.kotlinsignald

import Buffer
import NodeJS.get
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import net.Socket
import org.inthewaves.kotlinsignald.clientprotocol.SuspendSocketCommunicator
import process
import kotlin.coroutines.resumeWithException

private const val NOT_SUPPORTED_ERROR_MSG = "not supported in JavaScript"

private typealias ErrorHandler = (Any) -> Unit

private class NodeSocket private constructor(private val rawSocket: Socket, val socketPath: String) {
    val throwingListener = createErrorListenerWithWrappedError {
        rawSocket.destroy()
        throw it
    }

    init {
        rawSocket.apply {
            removeAllListeners("error")
            addListener("error", throwingListener)
        }
    }

    fun close() {
        rawSocket.destroy()
    }

    suspend fun writeAndReadReply(request: String): String {
        return suspendCancellableCoroutine { cont ->
            rawSocket.apply {
                write("$request\n")
                once("data") { buffer: Buffer -> cont.resume("$buffer", null) }
            }
        }
    }

    suspend fun readLine(): String? {
        return withTimeout(2000L) {
            suspendCancellableCoroutine { cont ->
                val lineReader = { buffer: Buffer -> cont.resume("$buffer", null) }
                rawSocket.once("data", lineReader)
            }
        }
    }

    companion object {
        private inline fun createErrorListenerWithWrappedError(
            crossinline handleException: (wrapped: SocketUnavailableException) -> Unit
        ): ErrorHandler = { error: Any ->
            if (error is CancellationException) throw error
            @Suppress("ThrowableNotThrown")
            val exception = if (error is Throwable) {
                SocketUnavailableException(error)
            } else {
                SocketUnavailableException("$error")
            }
            handleException(exception)
        }

        private suspend fun makeNewNetSocketConnection(socketPath: String): Socket {
            return suspendCancellableCoroutine { cont ->
                @Suppress("JoinDeclarationAndAssignment")
                lateinit var socket: Socket
                val errorListener = createErrorListenerWithWrappedError { wrapped ->
                    socket.destroy()
                    cont.resumeWithException(wrapped)
                }
                socket = net.createConnection(socketPath)
                socket.once("error", errorListener)
                    .once("data") { _: Buffer ->
                        // ignore version line
                        val throwingListener = createErrorListenerWithWrappedError { throw it }
                        socket.addListener("error", throwingListener)
                        socket.removeListener("error", errorListener)
                        cont.resume(socket, onCancellation = { socket.destroy() })
                    }
            }
        }

        /**
         * @throws SocketUnavailableException
         */
        suspend fun create(socketPath: String?): NodeSocket {
            if (socketPath != null) {
                return NodeSocket(makeNewNetSocketConnection(socketPath), socketPath)
            }

            return getDefaultSocketPaths()
                .firstNotNullOfOrNull { defaultPathToTest ->
                    try {
                        NodeSocket(makeNewNetSocketConnection(defaultPathToTest), defaultPathToTest)
                    } catch (e: SocketUnavailableException) {
                        null
                    }
                }
                ?: throw SocketUnavailableException("unable to make a connection from default socket paths")
        }
    }
}

public class SocketWrapper private constructor(
    public val actualSocketPath: String
) : SuspendSocketCommunicator {
    override suspend fun submitSuspend(request: String): String = useNewSocketConnection { socket ->
        socket.writeAndReadReply(request)
    }

    override suspend fun readLineSuspend(): String? = useNewSocketConnection { socket -> socket.readLine() }

    override fun submit(request: String): String {
        throw UnsupportedOperationException(NOT_SUPPORTED_ERROR_MSG)
    }

    override fun readLine(): String? {
        throw UnsupportedOperationException(NOT_SUPPORTED_ERROR_MSG)
    }

    private suspend inline fun <T> useNewSocketConnection(block: (socket: NodeSocket) -> T): T {
        val socket = NodeSocket.create(actualSocketPath)
        try {
            return block(socket)
        } finally {
            socket.close()
        }
    }

    public companion object {
        public fun create(socketPath: String?): SocketWrapper {
            throw UnsupportedOperationException(NOT_SUPPORTED_ERROR_MSG)
        }

        public suspend fun createAsync(socketPath: String?): SocketWrapper {
            val nodeSocket = NodeSocket.create(socketPath)
            nodeSocket.close()
            return SocketWrapper(nodeSocket.socketPath)
        }
    }
}

public class PersistentSocketWrapper private constructor(
    private val socket: NodeSocket
) : SuspendSocketCommunicator {
    override suspend fun submitSuspend(request: String): String = socket.writeAndReadReply(request)

    override suspend fun readLineSuspend(): String? = socket.readLine()

    override fun submit(request: String): String {
        throw UnsupportedOperationException(NOT_SUPPORTED_ERROR_MSG)
    }

    override fun readLine(): String? {
        throw UnsupportedOperationException(NOT_SUPPORTED_ERROR_MSG)
    }

    public fun close() {
        socket.close()
    }

    public companion object {
        public fun create(socketPath: String?): PersistentSocketWrapper {
            throw UnsupportedOperationException(NOT_SUPPORTED_ERROR_MSG)
        }

        public suspend fun createAsync(socketPath: String?): PersistentSocketWrapper {
            return PersistentSocketWrapper(NodeSocket.create(socketPath))
        }
    }
}

internal actual fun getEnvVariable(envVarName: String): String? = process.env[envVarName]
