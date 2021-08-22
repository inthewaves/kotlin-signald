package org.inthewaves.kotlinsignald

import org.inthewaves.kotlinsignald.clientprotocol.SignaldException
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator

public class SocketUnavailableException : SignaldException {
    public constructor() : super()

    public constructor(message: String?) : super(message)

    public constructor(message: String?, cause: Throwable?) : super(message, cause)

    public constructor(cause: Throwable?) : super(cause)
}

/**
 * A wrapper for a socket that creates a new socket connection for every request.
 */
internal expect class SocketWrapper @Throws(SocketUnavailableException::class) constructor(
    socketPath: String? = null
) : SocketCommunicator

/**
 * A wrapper for a socket that maintains a socket connection for every request, ideal for receiving chat messages
 * from a subscribe request.
 */
internal expect class PersistentSocketWrapper @Throws(SocketUnavailableException::class) constructor(
    socketPath: String? = null
) : SocketCommunicator {
    fun close()
}
