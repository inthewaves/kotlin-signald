package org.inthewaves.kotlinsignald.subscription

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import org.inthewaves.kotlinsignald.SignaldClient
import org.inthewaves.kotlinsignald.clientprotocol.SignaldException
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ClientMessageWrapper
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Launches a new coroutine that uses the [signaldClient] to create a new socket connection and using the socket to
 * subscribe to incoming messages from signald. The incoming messages are sent through the returned [ReceiveChannel].
 *
 * The coroutine context is inherited from a [CoroutineScope]. Additional context elements can be specified with the
 * [context] parameter.
 *
 * Cancellation of the scope or cancelling the channel will unsubscribe from incoming messages and close the socket.
 *
 * It's recommended that this function is called from a coroutine context that can handle blocking IO calls such as
 * `Dispatchers.IO` on JVM or using a new single-threaded context.
 *
 * @see [ChannelMessageSubscriptionHandler]
 * @param signaldClient The [SignaldClient] instance. Must be associated with an account registered with signald.
 * @param bufferCapacity Size of the buffer for the channel. (optional, cannot be negative, defaults to
 * [Channel.RENDEZVOUS], i.e. zero). This option should be used with care; setting a higher buffer means that the
 * client will take messages from signald that will be buffered locally, but if the client crashes and never handles
 * those messages, signald will not resend those messages.
 * @param onBufferOverflow configures an action on buffer overflow (optional, defaults to a suspending attempt to send a
 * value, supported only when `capacity >= 0` or `capacity == Channel.BUFFERED`, implicitly creates a channel with at
 * least one buffered element)
 * @param onUndeliveredElement An optional function that is called when element was sent but was not delivered to the
 * consumer. See "Undelivered elements" section in Channel documentation.
 * @throws SignaldException if subscription fails (e.g., creating the persistent socket fails)
 */
public fun CoroutineScope.signalMessagesChannel(
    signaldClient: SignaldClient,
    context: CoroutineContext = EmptyCoroutineContext,
    bufferCapacity: Int = Channel.RENDEZVOUS,
    onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND,
    onUndeliveredElement: ((ClientMessageWrapper) -> Unit)? = null
): ReceiveChannel<ClientMessageWrapper> =
    ChannelMessageSubscriptionHandler(
        signaldClient = signaldClient,
        coroutineScope = this,
        context = context,
        bufferCapacity = bufferCapacity,
        onBufferOverflow = onBufferOverflow,
        onUndeliveredElement = onUndeliveredElement
    ).messages

/**
 * Creates a [Channel]-based message handler. Messages can be received by receiving elements from the [messages]
 * [ReceiveChannel]. Unsubscription and closing of the socket is handled by calling [close] or cancelling the given
 * `coroutineScope`.
 *
 * @param signaldClient The [SignaldClient] instance. Must be associated with an account registered with signald.
 * @param coroutineScope The [CoroutineScope] to use for the message subscription coroutine.
 * @param context An additional [CoroutineContext] that will be added to the given `coroutineScope`'s context.
 * @param bufferCapacity Size of the buffer for the channel. (optional, cannot be negative, defaults to
 * [Channel.RENDEZVOUS], i.e. zero). This option should be used with care; setting a higher buffer means that the
 * client will take messages from signald that will be buffered locally, but if the client crashes and never handles
 * those messages, signald will not resend those messages.
 * @param onBufferOverflow configures an action on buffer overflow (optional, defaults to a suspending attempt to send a
 * value, supported only when `capacity >= 0` or `capacity == Channel.BUFFERED`, implicitly creates a channel with at
 * least one buffered element)
 * @param onUndeliveredElement An optional function that is called when element was sent but was not delivered to the
 * consumer. See "Undelivered elements" section in Channel documentation.
 * @throws SignaldException if subscription fails (e.g., creating the persistent socket fails)
 */
public class ChannelMessageSubscriptionHandler(
    signaldClient: SignaldClient,
    coroutineScope: CoroutineScope,
    context: CoroutineContext = EmptyCoroutineContext,
    bufferCapacity: Int = Channel.RENDEZVOUS,
    onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND,
    onUndeliveredElement: ((ClientMessageWrapper) -> Unit)? = null
) : CoroutineMessageSubscriptionHandler(signaldClient, coroutineScope, context) {

    private val _messages: Channel<ClientMessageWrapper> = Channel(
        capacity = bufferCapacity,
        onBufferOverflow = onBufferOverflow,
        onUndeliveredElement = onUndeliveredElement
    )

    /**
     * A [ReceiveChannel] of incoming messages. As [ClientMessageWrapper] is a sealed type, using a `when` statement
     * on the message will be exhaustive.
     */
    public val messages: ReceiveChannel<ClientMessageWrapper> = _messages

    init {
        // needs to be done because of derived class initialization order, otherwise _messages can be null
        emissionJob.start()
    }

    override suspend fun sendMessageToSubscribers(newMessage: ClientMessageWrapper): Boolean {
        return if (_messages.isClosedForSend) {
            false
        } else {
            _messages.send(newMessage)
            true
        }
    }

    override fun onCompletion() {
        _messages.close()
    }
}
