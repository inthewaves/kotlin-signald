@file:JvmName("SocketWrapperUtil")

package org.inthewaves.kotlinsignald

import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import kotlin.jvm.JvmName
import kotlin.jvm.JvmStatic

/**
 * A wrapper for a socket that makes new socket connections for every request and closes the connection after a request.
 * making it thread safe.
 */
public expect class SocketWrapper : SocketCommunicator {
    public val actualSocketPath: String

    public override fun close()

    public companion object {
        @JvmStatic
        public fun create(socketPath: String?): SocketWrapper
    }
}

/**
 * A wrapper for a socket that maintains a socket connection for every request, ideal for receiving chat messages
 * after a subscribe request.
 */
public expect class PersistentSocketWrapper : SocketCommunicator {
    public override fun close()

    public companion object {
        @JvmStatic
        public fun create(socketPath: String?): PersistentSocketWrapper
    }
}
