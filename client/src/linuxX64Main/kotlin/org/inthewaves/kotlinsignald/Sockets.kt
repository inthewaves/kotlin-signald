package org.inthewaves.kotlinsignald

import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator

/**
 * A wrapper for a socket that maintains a socket connection for every request, ideal for receiving chat messages
 * after a subscribe request.
 */
public actual class PersistentSocketWrapper @Throws(SocketUnavailableException::class) actual constructor(socketPath: String?) :
    SocketCommunicator {
    public actual fun close() {
    }

    /**
     * Sends the [request] to the socket as a single line of JSON (line terminated with \n), and
     * returns the JSON response from signald.
     * @throws [SignaldException] if an I/O error occurs during socket communication
     */
    override fun submit(request: String): String {
        TODO("Not yet implemented")
    }

    /**
     * Reads a JSON message from the socket, blocking until a message is received or returning null
     * if the socket closes. Might not be supported by the implementation.
     * @throws [SignaldException] if an I/O error occurs during socket communication
     * @throws [UnsupportedOperationException] the communicator doesn't support this operation (can
     * happen if the communicator closes connections after a request is handled).
     */
    override fun readLine(): String? {
        TODO("Not yet implemented")
    }
}

/**
 * A wrapper for a socket that creates a new socket connection for every request.
 */
public actual class SocketWrapper @Throws(SocketUnavailableException::class) actual constructor(socketPath: String?) :
    SocketCommunicator {
    override fun submit(request: String): String {
        TODO("Not yet implemented")
    }

    override fun readLine(): String? {
        TODO("Not yet implemented")
    }
}
