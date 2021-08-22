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
 * @param socketPath An optional path to the signald socket. If this is null, it will attempt the default socket
 * locations (`$XDG_RUNTIME_DIR/signald/signald.sock` and `/var/run/signald/signald.sock`)
 */
public expect class SocketWrapper @Throws(SocketUnavailableException::class) constructor(
    socketPath: String? = null
) : SocketCommunicator {
    public val actualSocketPath: String
}

/**
 * A wrapper for a socket that maintains a socket connection for every request, ideal for receiving chat messages
 * after a subscribe request.
 */
public expect class PersistentSocketWrapper @Throws(SocketUnavailableException::class) constructor(
    socketPath: String? = null
) : SocketCommunicator {
    public fun close()
}
