package org.inthewaves.kotlinsignald.subscription

import org.inthewaves.kotlinsignald.IncomingMessageSubscription
import org.inthewaves.kotlinsignald.NodePersistentSocketWrapper
import org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ClientMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.SubscriptionResponse
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.UnsubscribeRequest

public actual class Subscription internal constructor(
    public actual val accountId: String,
    private val persistentSocket: NodePersistentSocketWrapper,
    initialMessages: Collection<ClientMessageWrapper>
) : IncomingMessageSubscription {
    /**
     * The number of messages sent while we were waiting for signald's response to the subscribe.
     */
    public override val initialMessagesCount: Int = initialMessages.size

    private var initialMessagesState = initialMessages.ifEmpty { null }?.toMutableList()

    /**
     * Parses an incoming message from the socket. If this returns null, then the socket is closed / reached EOF.
     * This will suspend if it has to wait for messages.
     *
     * @throws RequestFailedException if an incoming message can't be serialized (subclass of [SignaldException])
     * @throws SignaldException if an I/O error occurs when communicating with the socket.
     */
    public override suspend fun nextMessageSuspend(): ClientMessageWrapper? {
        val initialMessages = initialMessagesState
        if (initialMessages != null) {
            val message = initialMessages.removeFirstOrNull()
            if (message != null) {
                return message
            } else {
                initialMessagesState = null
            }
        }

        val newJsonLine = persistentSocket.readLineSuspend() ?: return null
        return ClientMessageWrapper.decodeFromStringOrThrow(newJsonLine)
    }

    public suspend fun unsubscribe(): SubscriptionResponse {
        return UnsubscribeRequest(account = accountId).submitSuspend(persistentSocket)
    }

    public actual override fun close() {
        persistentSocket.close()
    }

    override fun nextMessage(): ClientMessageWrapper? {
        throw UnsupportedOperationException("not supported on JS")
    }
}
