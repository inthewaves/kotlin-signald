package org.inthewaves.kotlinsignald.clientprotocol

import java.io.IOException

public interface SocketCommunicator {
    /**
     * Sends the [request] to the socket as a single line of JSON (line terminated with \n), and
     * returns the JSON response from signald.
     * @throws IOException if an I/O error occurs during socket communication
     */
    @Throws(IOException::class)
    public fun submit(request: String): String

    /**
     * Reads a JSON message from the socket, blocking until a message is received or returning null
     * if the socket closes.
     * @throws IOException if an I/O error occurs during socket communication
     */
    @Throws(IOException::class)
    public fun readLine(): String?
}
