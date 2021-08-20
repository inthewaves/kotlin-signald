package org.inthewaves.kotlinsignald.clientprotocol

public actual interface SocketCommunicator {
    /**
     * Sends the [request] to the socket as a single line of JSON (line terminated with \n), and
     * returns the JSON response from signald.
     */
    public actual fun submit(request: String): String

    /**
     * Reads a JSON message from the socket, blocking until a message is received or returning null
     * if the socket closes.
     */
    public actual fun readLine(): String?
}
