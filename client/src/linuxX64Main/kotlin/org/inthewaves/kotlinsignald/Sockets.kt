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
import platform.linux.sockaddr_un
import platform.posix.AF_UNIX
import platform.posix.EINTR
import platform.posix.SOCK_STREAM
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

internal fun makeNewSocketConnection(socketPath: String): Int? {
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
internal fun readLineFromSocket(socketFd: Int): String? {
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

internal fun getValidPathAndFdOrNull(socketPath: String?): Pair<String, Int>? {
    val socketPathsToTry = if (socketPath != null) sequenceOf(socketPath) else getDefaultSocketPaths()

    @Suppress("UNCHECKED_CAST")
    return socketPathsToTry
        .map { it to makeNewSocketConnection(it) }
        .firstOrNull { it.second != null } as Pair<String, Int>?
}

internal fun sendLineAndReadLineToSocket(socketFd: Int, request: String): String {
    val requestBytes = "$request\n".encodeToByteArray()
    requestBytes.usePinned { write(socketFd, it.addressOf(0), requestBytes.size.convert()) }
    return readLineFromSocket(socketFd) ?: throw SignaldException("socket EOF")
}

internal actual fun getEnvVariable(envVarName: String): String? = getenv(envVarName)?.toKStringFromUtf8()
