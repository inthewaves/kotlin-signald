package org.inthewaves.kotlinsignald

import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator

/**
 * A wrapper for a socket that maintains a socket connection for every request, ideal for receiving chat messages
 * from a subscribe request.
 */
internal actual class PersistentSocketWrapper @Throws(SocketUnavailableException::class) actual constructor(
    socketPath: String?
) : SocketCommunicator {
    actual fun close() {
    }

    override fun submit(request: String): String {
        TODO("Not yet implemented")
    }

    override fun readLine(): String? {
        TODO("Not yet implemented")
    }
}

/**
 * A wrapper for a socket that creates a new socket connection for every request.
 */
internal actual class SocketWrapper @Throws(SocketUnavailableException::class) actual constructor(socketPath: String?) :
    SocketCommunicator {
    override fun submit(request: String): String {
        TODO("Not yet implemented")
    }

    override fun readLine(): String? {
        TODO("Not yet implemented")
    }
}
