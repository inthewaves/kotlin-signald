package org.inthewaves.kotlinsignald

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.readValue
import kotlinx.cinterop.toKString
import kotlinx.cinterop.toKStringFromUtf8
import kotlinx.cinterop.usePinned
import kotlinx.cinterop.value
import org.inthewaves.kotlinsignald.clientprotocol.SignaldException
import org.inthewaves.kotlinsignald.clientprotocol.SuspendSocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonVersionMessage
import platform.linux.sockaddr_un
import platform.posix.AF_UNIX
import platform.posix.EINTR
import platform.posix.SOCK_STREAM
import platform.posix.close
import platform.posix.connect
import platform.posix.getenv
import platform.posix.posix_errno
import platform.posix.read
import platform.posix.sockaddr
import platform.posix.socket
import platform.posix.ssize_t
import platform.posix.strcpy
import platform.posix.strerror
import platform.posix.write

private fun makeNewSocketConnection(socketPath: String): Int? {
    val socketFd = socket(AF_UNIX, SOCK_STREAM, 0)
    if (socketFd == -1) {
        return null
    }

    memScoped {
        val remoteAddr = alloc<sockaddr_un>().apply {
            sun_family = AF_UNIX.convert()
            strcpy(sun_path, socketPath)
        }

        @Suppress("UNCHECKED_CAST")
        val remoteAddrAsSockAddr = remoteAddr.ptr as CPointer<sockaddr>
        return if (connect(socketFd, remoteAddrAsSockAddr, remoteAddr.readValue().size.convert()) != -1) {
            return socketFd
        } else {
            null
        }
    }
}

/**
 * @return the line or null if eof
 */
private fun readLineFromSocket(socketFd: Int): String? {
    return buildString {
        memScoped {
            val chr = alloc<ByteVar>()
            while (true) {
                val numRead: ssize_t = read(socketFd, chr.ptr, 1)
                if (numRead == -1L) {
                    if (posix_errno() == EINTR) {
                        continue
                    } else {
                        throw SignaldException(
                            "error while reading from socket: ${strerror(posix_errno())?.toKString()}"
                        )
                    }
                } else if (numRead == 0L) { // EOF
                    return null
                } else { // numRead == 1
                    if (chr.value == 0.toByte()) {
                        break
                    }

                    val kotlinChar = chr.value.toInt().toChar()
                    append(kotlinChar)

                    if (kotlinChar == '\n') {
                        break
                    }
                }
            }
        }
    }
}

private fun getValidPathAndFdOrNull(socketPath: String?): Pair<String, Int>? {
    val socketPathsToTry = if (socketPath != null) sequenceOf(socketPath) else getDefaultSocketPaths()

    @Suppress("UNCHECKED_CAST")
    return socketPathsToTry
        .map { it to makeNewSocketConnection(it) }
        .firstOrNull { it.second != null } as Pair<String, Int>?
}

private fun sendLineAndReadLineToSocket(socketFd: Int, request: String): String {
    val requestBytes = "$request\n".encodeToByteArray()
    requestBytes.usePinned { write(socketFd, it.addressOf(0), requestBytes.size.convert()) }
    return readLineFromSocket(socketFd) ?: throw SignaldException("socket EOF")
}

public actual class SocketWrapper @Throws(SocketUnavailableException::class) private actual constructor(
    socketPath: String?
) : SuspendSocketCommunicator {
    public val version: JsonVersionMessage?
    public actual val actualSocketPath: String

    init {
        val result = getValidPathAndFdOrNull(socketPath) ?: throw SocketUnavailableException("can't")
        actualSocketPath = result.first
        version = decodeVersionOrNull(readLineFromSocket(result.second))
    }

    override suspend fun submitSuspend(request: String): String = submit(request)

    override suspend fun readLineSuspend(): String? = readLine()

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

public actual class PersistentSocketWrapper private actual constructor(
    socketPath: String?
) : SuspendSocketCommunicator {
    public val version: JsonVersionMessage?
    // private val arena = Arena()
    private val socketFd: Int

    init {
        val result = getValidPathAndFdOrNull(socketPath) ?: throw SocketUnavailableException("can't")
        socketFd = result.second
        // Skip the version line
        version = decodeVersionOrNull(readLineFromSocket(socketFd))
    }

    override suspend fun submitSuspend(request: String): String {
        TODO("Not yet implemented")
    }

    override suspend fun readLineSuspend(): String? {
        TODO("Not yet implemented")
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

internal actual fun getEnvVariable(envVarName: String): String? = getenv(envVarName)?.toKStringFromUtf8()
