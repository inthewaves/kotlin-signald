// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol

/**
 * An interface to facilitate communication with signald socket. The implementation might close
 * socket connections after making a request, in which case, the [readLineSuspend] function will not be
 * supported.
 *
 * This variant has suspending operations for JavaScript (Node.js) support.
 */
public interface SuspendSocketCommunicator : AutoCloseable {
    /**
     * Sends the [request] to the socket as a single line of JSON (line terminated with \n), and
     * returns the JSON response from signald.
     *
     * @throws [SignaldException] if an I/O error occurs during socket communication
     */
    public suspend fun submitSuspend(request: String): String

    /**
     * Reads a JSON message from the socket, blocking until a message is received or returning null
     * if the socket closes. Might not be supported by the implementation.
     * @throws [SignaldException] if an I/O error occurs during socket communication
     * @throws [UnsupportedOperationException] the communicator doesn't support this operation (can
     * happen if the communicator closes connections after a request is handled).
     */
    public suspend fun readLineSuspend(): String?
}
