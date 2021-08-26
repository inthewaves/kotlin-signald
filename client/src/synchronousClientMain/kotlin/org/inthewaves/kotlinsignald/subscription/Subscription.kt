package org.inthewaves.kotlinsignald.subscription

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.serialization.SerializationException
import org.inthewaves.kotlinsignald.IncomingMessageSubscription
import org.inthewaves.kotlinsignald.PersistentSocketWrapper
import org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException
import org.inthewaves.kotlinsignald.clientprotocol.SignaldJson
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ClientMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.SubscriptionResponse
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.UnsubscribeRequest

/**
 * Contains information about an active incoming message subscription with signald.
 */
public class Subscription internal constructor(
    public val accountId: String,
    private val persistentSocket: PersistentSocketWrapper,
    initialMessages: Collection<ClientMessageWrapper>
) : IncomingMessageSubscription {
    /**
     * The number of messages sent while we were waiting for signald's response to the subscribe.
     */
    public override val initialMessagesCount: Int = initialMessages.size

    private var initialMessagesState = initialMessages
        .ifEmpty { null }
        ?.let { it.toMutableList() to SynchronizedObject() }

    override suspend fun nextMessageSuspend(): ClientMessageWrapper? = nextMessage()

    /**
     * Parses an incoming message from the socket. If this returns null, then the socket is closed / reached EOF.
     * This will block the current thread if it has to wait for messages.
     *
     * @throws RequestFailedException if an incoming message can't be serialized (subclass of [SignaldException])
     * @throws SignaldException if an I/O error occurs when communicating with the socket.
     */
    public fun nextMessage(): ClientMessageWrapper? {
        val msgState = initialMessagesState
        if (msgState != null) {
            val (list, lock) = msgState
            kotlinx.atomicfu.locks.synchronized(lock) {
                val message = list.removeFirstOrNull()
                if (message != null) {
                    return message
                } else {
                    initialMessagesState = null
                }
            }
        }

        val newJsonLine = persistentSocket.readLine() ?: return null
        return try {
            SignaldJson.decodeFromString(ClientMessageWrapper.serializer(), newJsonLine)
        } catch (e: SerializationException) {
            throw RequestFailedException(
                responseJsonString = newJsonLine,
                extraMessage = "failed to parse incoming message",
                cause = e,
            )
        }
    }

    public fun unsubscribe(): SubscriptionResponse {
        return UnsubscribeRequest(account = accountId).submit(persistentSocket)
    }

    public override fun close() {
        persistentSocket.close()
    }
}
