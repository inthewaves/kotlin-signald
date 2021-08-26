package org.inthewaves.kotlinsignald

import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonVersionMessage
import platform.posix.close

public actual class SocketWrapper @Throws(SocketUnavailableException::class) private constructor(
    socketPath: String?
) : SocketCommunicator {
    public val version: JsonVersionMessage?
    public actual val actualSocketPath: String

    init {
        val (path, socketFd) = getValidPathAndFdOrNull(socketPath) ?: throw SocketUnavailableException("can't")
        actualSocketPath = path
        try {
            version = decodeVersionOrNull(readLineFromSocket(socketFd))
        } finally {
            close(socketFd)
        }
    }

    override fun submit(request: String): String {
        val socketFd = makeNewSocketConnection(actualSocketPath)
            ?: throw SocketUnavailableException("unable to get socket")
        try {
            // ignore version line from reconnecting
            readLineFromSocket(socketFd)
            return sendLineAndReadLineToSocket(socketFd, request)
        } finally {
            close(socketFd)
        }
    }

    override fun readLine(): String? {
        throw UnsupportedOperationException("wrong SocketWrapper type")
    }

    public actual companion object {
        @Throws(SocketUnavailableException::class)
        public actual fun create(socketPath: String?): SocketWrapper = SocketWrapper(socketPath)
    }
}

public actual class PersistentSocketWrapper private constructor(
    socketPath: String?
) : SocketCommunicator {
    public val version: JsonVersionMessage?
    // private val arena = Arena()
    private val socketFd: Int

    init {
        val (_, validSocketFd) = getValidPathAndFdOrNull(socketPath) ?: throw SocketUnavailableException("can't")
        socketFd = validSocketFd
        // Skip the version line
        version = decodeVersionOrNull(readLineFromSocket(validSocketFd))
    }

    override fun submit(request: String): String = sendLineAndReadLineToSocket(socketFd, request)

    override fun readLine(): String? = readLineFromSocket(socketFd)

    public actual fun close() {
        close(socketFd)
        // arena.clear()
    }

    public actual companion object {
        @Throws(SocketUnavailableException::class)
        public actual fun create(socketPath: String?): PersistentSocketWrapper = PersistentSocketWrapper(socketPath)
    }
}
