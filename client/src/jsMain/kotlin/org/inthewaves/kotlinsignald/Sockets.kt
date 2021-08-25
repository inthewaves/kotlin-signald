package org.inthewaves.kotlinsignald

import Buffer
import NodeJS.get
import net.Socket
import org.inthewaves.kotlinsignald.clientprotocol.SuspendSocketCommunicator
import process
import kotlin.js.Promise

private const val NOT_SUPPORTED_ERROR_MSG = "not supported in JavaScript"

private inline fun createErrorListener(
    crossinline handleException: (SocketUnavailableException) -> Unit
) = { error: Any ->
    @Suppress("ThrowableNotThrown")
    val exception =  if (error is Throwable) {
        SocketUnavailableException(error)
    } else {
        SocketUnavailableException("$error")
    }
    handleException(exception)
}

private fun makeNewSocketConnection(socketPath: String): Promise<Socket> {
     return Promise<Socket> { resolve, reject ->
         val socket = net.createConnection(socketPath)
         val errorListener = createErrorListener { exception ->
             socket.destroy()
             reject(exception)
         }
         // Add a listener for data so that we drop the version line
         socket
             .once("error", errorListener)
             .once("data") { _: Buffer ->
                 socket.removeListener("error", errorListener)
                 resolve(socket)
             }
    }
}

private fun getSocketOrThrow(socketPath: String?): Promise<Pair<String, Socket>> {
    if (socketPath != null) {
        return makeNewSocketConnection(socketPath).then { socketPath to it }
    } else {
        val promises = getDefaultSocketPaths().toList()
            .map { defaultPathToTry ->
                makeNewSocketConnection(defaultPathToTry)
                    .then(onFulfilled = { defaultPathToTry to it }, onRejected = { null })
            }
            .toTypedArray()

        return Promise.all(promises).then { array ->
            val nonNull = array.firstOrNull { it != null }
            // If there were multiple successes, only pick the first one and close the other connections
            array.forEach { if (it != null && it != nonNull) it.second.destroy() }
            return@then nonNull ?: throw SocketUnavailableException("unable")
        }
    }

    /*
    for (currentPath in socketPathsToTry) {
        val socket = makeNewSocketConnection(currentPath).then(onFulfilled = { it }, onRejected = { null }).await()
        if (socket != null) {
            return Promise.resolve(currentPath to socket)
        }
    }

    return Promise.reject(
        SocketUnavailableException(
            if (socketPath != null) "tried $socketPath but wasn't available" else "tried default paths but not available"
        )
    )
     */
}

private fun readLineFromSocket(socket: Socket): Promise<String> {
    return Promise<String> { resolve, reject ->
        socket
            .once("data") { buffer: Buffer -> resolve("$buffer") }
    }
    TODO("SOCK")
}

public actual class SocketWrapper private constructor(
    public actual val actualSocketPath: String
) : SuspendSocketCommunicator {

    override suspend fun submitSuspend(request: String): String {
        TODO("Not yet implemented")
    }

    override suspend fun readLineSuspend(): String? {
        TODO("Not yet implemented")
    }

    override fun submit(request: String): String {
        throw UnsupportedOperationException(NOT_SUPPORTED_ERROR_MSG)
    }

    override fun readLine(): String? {
        throw UnsupportedOperationException(NOT_SUPPORTED_ERROR_MSG)
    }

    public actual companion object {
        public actual fun create(socketPath: String?): SocketWrapper {
            throw UnsupportedOperationException(NOT_SUPPORTED_ERROR_MSG)
        }

        public fun createAsync(socketPath: String?): Promise<SocketWrapper> {
            return getSocketOrThrow(socketPath)
                .then { (goodSocketPath, socket) ->
                    socket.destroy()
                    SocketWrapper(goodSocketPath)
                }
        }
    }
}

public actual class PersistentSocketWrapper private constructor(private val socket: Socket) : SuspendSocketCommunicator {
    override suspend fun submitSuspend(request: String): String {
        TODO("Not yet implemented")
    }

    override suspend fun readLineSuspend(): String? {
        TODO("Not yet implemented")
    }

    override fun submit(request: String): String {
        throw UnsupportedOperationException(NOT_SUPPORTED_ERROR_MSG)
    }

    override fun readLine(): String? {
        throw UnsupportedOperationException(NOT_SUPPORTED_ERROR_MSG)
    }

    public actual fun close() {
        throw UnsupportedOperationException(NOT_SUPPORTED_ERROR_MSG)
    }

    public actual companion object {
        public actual fun create(socketPath: String?): PersistentSocketWrapper {
            throw UnsupportedOperationException(NOT_SUPPORTED_ERROR_MSG)
        }

        public fun createAsync(socketPath: String?): Promise<PersistentSocketWrapper> {
            return getSocketOrThrow(socketPath)
                .then { (_, socket) -> PersistentSocketWrapper(socket) }
        }
    }
}

internal actual fun getEnvVariable(envVarName: String): String? = process.env[envVarName]
