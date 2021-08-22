package org.inthewaves.kotlinsignald

import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator

public actual class PersistentSocketWrapper actual constructor(socketPath: String?) : SocketCommunicator {
    public actual fun close() {
    }

    override fun submit(request: String): String {
        TODO("Not yet implemented")
    }

    override fun readLine(): String? {
        TODO("Not yet implemented")
    }
}

public actual class SocketWrapper actual constructor(socketPath: String?) : SocketCommunicator {

    public actual val actualSocketPath: String = TODO()

    override fun submit(request: String): String {
        TODO("Not yet implemented")
    }

    override fun readLine(): String? {
        TODO("Not yet implemented")
    }
}
