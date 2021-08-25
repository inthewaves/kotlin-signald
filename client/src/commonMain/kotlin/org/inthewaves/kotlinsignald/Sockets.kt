@file:JvmName("SocketUtil")
package org.inthewaves.kotlinsignald

import kotlinx.serialization.SerializationException
import org.inthewaves.kotlinsignald.clientprotocol.SignaldException
import org.inthewaves.kotlinsignald.clientprotocol.SignaldJson
import org.inthewaves.kotlinsignald.clientprotocol.SuspendSocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonVersionMessage
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

internal fun decodeVersionOrNull(versionLine: String?) = if (versionLine != null) {
    try {
        SignaldJson.decodeFromString(JsonMessageWrapper.serializer(JsonVersionMessage.serializer()), versionLine).data
    } catch (e: SerializationException) {
        null
    }
} else {
    null
}

public class SocketUnavailableException : SignaldException {
    public constructor() : super()

    public constructor(message: String?) : super(message)

    public constructor(message: String?, cause: Throwable?) : super(message, cause)

    public constructor(cause: Throwable?) : super(cause)
}

internal expect fun getEnvVariable(envVarName: String): String?

internal fun getDefaultSocketPaths(): Sequence<String> =
    sequenceOf(
        getEnvVariable("XDG_RUNTIME_DIR")?.let { "$it/signald/signald.sock" },
        "/var/run/signald/signald.sock"
    ).filterNotNull()

/**
 * A wrapper for a socket that makes new socket connections for every request and closes the connection after a request.
 * making it thread safe.
 *
 * @param socketPath An optional path to the signald socket. If this is null, it will attempt the default socket
 * locations (`$XDG_RUNTIME_DIR/signald/signald.sock` and `/var/run/signald/signald.sock`)
 */
public expect class SocketWrapper @Throws(SocketUnavailableException::class) private constructor(
    socketPath: String? = null
) : SuspendSocketCommunicator {
    public val actualSocketPath: String

    public companion object {
        @JvmStatic
        public fun create(socketPath: String?): SocketWrapper
    }
}

/**
 * A wrapper for a socket that maintains a socket connection for every request, ideal for receiving chat messages
 * after a subscribe request.
 */
public expect class PersistentSocketWrapper @Throws(SocketUnavailableException::class) private constructor(
    socketPath: String? = null
) : SuspendSocketCommunicator {
    public fun close()

    public companion object {
        @JvmStatic
        public fun create(socketPath: String?): PersistentSocketWrapper
    }
}
