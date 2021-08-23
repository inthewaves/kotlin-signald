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
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
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

private fun getSocketFdOrNull(socketPath: String): Int? {
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
        val un = remoteAddr.ptr as CPointer<sockaddr>
        if (connect(socketFd, un, remoteAddr.readValue().size.convert()) != -1) {
            return socketFd
        }
    }
    return null
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
                            "error while reading from socket: errno ${strerror(posix_errno())?.toKString()}"
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
    val socketPathsToTry: Sequence<String> = socketPath?.let { sequenceOf(it) }
        ?: sequenceOf(
            getenv("XDG_RUNTIME_DIR")?.toKStringFromUtf8(),
            "/var/run/signald/signald.sock"
        ).filterNotNull()

    @Suppress("UNCHECKED_CAST")
    return socketPathsToTry
        .map { it to getSocketFdOrNull(it) }
        .firstOrNull { it.second != null } as Pair<String, Int>?
}

private fun sendLineAndReadLineToSocket(socketFd: Int, request: String): String {
    val requestBytes = "$request\n".encodeToByteArray()
    requestBytes.usePinned { write(socketFd, it.addressOf(0), requestBytes.size.convert()) }
    return readLineFromSocket(socketFd) ?: throw SignaldException("socket EOF")
}

public actual class SocketWrapper @Throws(SocketUnavailableException::class) actual constructor(
    socketPath: String?
) : SocketCommunicator {
    public val version: JsonVersionMessage?
    public actual val actualSocketPath: String

    init {
        val result = getValidPathAndFdOrNull(socketPath) ?: throw SocketUnavailableException("can't")
        actualSocketPath = result.first
        version = decodeVersionOrNull(readLineFromSocket(result.second))
    }

    override fun submit(request: String): String {
        val socketFd = getSocketFdOrNull(actualSocketPath) ?: throw SocketUnavailableException("unable to get socket")
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
}

public actual class PersistentSocketWrapper @Throws(SocketUnavailableException::class) actual constructor(
    socketPath: String?
) : SocketCommunicator {
    public val version: JsonVersionMessage?
    // private val arena = Arena()
    private val socketFd: Int

    init {
        val result = getValidPathAndFdOrNull(socketPath) ?: throw SocketUnavailableException("can't")
        socketFd = result.second
        // Skip the version line
        version = decodeVersionOrNull(readLineFromSocket(socketFd))
    }

    override fun submit(request: String): String = sendLineAndReadLineToSocket(socketFd, request)

    override fun readLine(): String? = readLineFromSocket(socketFd)

    public actual fun close() {
        close(socketFd)
        // arena.clear()
    }
}
