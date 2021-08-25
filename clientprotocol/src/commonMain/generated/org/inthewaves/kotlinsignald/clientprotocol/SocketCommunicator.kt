package org.inthewaves.kotlinsignald.clientprotocol

/**
 * An interface to facilitate communication with signald socket. The implementation might close
 * socket connections after making a request, in which case, the [readLine] function will not be
 * supported.
 */
public interface SocketCommunicator {
    /**
     * Sends the [request] to the socket as a single line of JSON (line terminated with \n), and
     * returns the JSON response from signald.
     *
     * @throws [SignaldException] if an I/O error occurs during socket communication
     */
    @Throws(SignaldException::class)
    public fun submit(request: String): String

    /**
     * Reads a JSON message from the socket, blocking until a message is received or returning null
     * if the socket closes. Might not be supported by the implementation.
     * @throws [SignaldException] if an I/O error occurs during socket communication
     * @throws [UnsupportedOperationException] the communicator doesn't support this operation (can
     * happen if the communicator closes connections after a request is handled).
     */
    @Throws(SignaldException::class)
    public fun readLine(): String?
}
