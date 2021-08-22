package org.inthewaves.kotlinsignald

import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator

/**
 * A wrapper for a socket that maintains a socket connection for every request, ideal for receiving chat messages
 * after a subscribe request.
 */
public actual class PersistentSocketWrapper actual constructor(socketPath: String?) : SocketCommunicator {
    public actual fun close() {

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
public actual class SocketWrapper actual constructor(socketPath: String?) : SocketCommunicator {

    override fun submit(request: String): String {
        TODO("Not yet implemented")
    }

    override fun readLine(): String? {
        TODO("Not yet implemented")
    }
}
