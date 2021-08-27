package org.inthewaves.kotlinsignald.subscription

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.inthewaves.kotlinsignald.IncomingMessageSubscription
import org.inthewaves.kotlinsignald.SignaldClient
import org.inthewaves.kotlinsignald.clientprotocol.SignaldException
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ClientMessageWrapper
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

internal const val DEFAULT_REPLAY = 0

/**
 * Launches a new coroutine that uses the [IncomingMessageSubscription] to handle incoming messages from signald. The
 * incoming messages are emitted in the returned [SharedFlow].
 *
 * The coroutine context is inherited from a [CoroutineScope]. Additional context elements can be specified with the
 * [context] parameter.
 *
 * Cancellation of the scope will unsubscribe from incoming messages and close the socket.
 *
 * @see FlowMessageSubscriptionHandler
 * @param signal The [Signal] instance. Must be associated with an account registered with signald.
 * @param replay The number of values replayed to new subscribers (cannot be negative, defaults to zero).
 * @param extraBufferCapacity Size of the buffer for emissions to the messages shared flow, allowing slow subscribers
 * to get values from the buffer without suspending emitters. The buffer space determines how much slow subscribers can
 * lag from the fast ones. (optional, cannot be negative, defaults to 25)
 * @param onBufferOverflow configures an emit action on buffer overflow. Optional, defaults to suspending attempts to
 * emit a value. Values other than [BufferOverflow.SUSPEND] are supported only when `replay > 0` or
 * `extraBufferCapacity > 0`. Buffer overflow can happen only when there is at least one subscriber that is not ready to
 * accept the new value. In the absence of subscribers only the most recent replay values are stored and the buffer
 * overflow behavior is never triggered and has no effect.
 * @throws SignaldException if subscription fails (e.g., creating the persistent socket fails)
 */
public fun CoroutineScope.signalMessagesSharedFlow(
    signaldClient: SignaldClient,
    context: CoroutineContext = EmptyCoroutineContext,
    replay: Int = DEFAULT_REPLAY,
    extraBufferCapacity: Int = CoroutineMessageSubscriptionHandler.DEFAULT_BUFFER_CAPACITY,
    onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND
): SharedFlow<ClientMessageWrapper> =
    FlowMessageSubscriptionHandler(
        signaldClient = signaldClient,
        coroutineScope = this,
        context = context,
        replay = replay,
        extraBufferCapacity = extraBufferCapacity,
        onBufferOverflow = onBufferOverflow
    ).messages

/**
 * Creates a [SharedFlow]-based message handler by creating a new socket connection and sending a subscribe request.
 * Messages can be received by collecting from the [messages] flow. Unsubscription and closing of the socket is handled
 * by calling [close] or cancelling the given [coroutineScope]. This is suitable for when multiple subscribers to
 * incoming messages are desired.
 *
 * Note that the [messages] flow is hot, meaning that it will be active regardless of whether there are any subscribers.
 *
 * @param signalWrapper The [Signal] instance. Must be associated with an account registered with signald.
 * @param coroutineScope The [CoroutineScope] to use for the message subscription coroutine. This is used to cancel
 * the handler.
 * @param replay The number of values replayed to new subscribers (cannot be negative, defaults to zero).
 * @param extraBufferCapacity Size of the buffer for emissions to the messages shared flow, allowing slow subscribers
 * to get values from the buffer without suspending emitters. The buffer space determines how much slow subscribers can
 * lag from the fast ones. (optional, cannot be negative, defaults to 25)
 * @param onBufferOverflow configures an emit action on buffer overflow. Optional, defaults to suspending attempts to
 * emit a value. Values other than [BufferOverflow.SUSPEND] are supported only when `replay > 0` or
 * `extraBufferCapacity > 0`. **Buffer overflow can happen only when there is at least one subscriber that is not ready
 * to accept the new value**. In the absence of subscribers only the most recent replay values are stored and the buffer
 * overflow behavior is never triggered and has no effect.
 * @throws SignaldException if subscription fails (e.g., creating the persistent socket fails)
 */
public class FlowMessageSubscriptionHandler(
    signaldClient: SignaldClient,
    coroutineScope: CoroutineScope,
    context: CoroutineContext = EmptyCoroutineContext,
    replay: Int = DEFAULT_REPLAY,
    extraBufferCapacity: Int = DEFAULT_BUFFER_CAPACITY,
    onBufferOverflow: BufferOverflow = BufferOverflow.SUSPEND
) : CoroutineMessageSubscriptionHandler(signaldClient, coroutineScope, context) {

    private val _messages = MutableSharedFlow<ClientMessageWrapper>(
        replay = replay,
        extraBufferCapacity = extraBufferCapacity,
        onBufferOverflow = onBufferOverflow,
    )

    /**
     * A hot [SharedFlow] of incoming messages. As [ClientMessageWrapper] is a sealed type, using a `when` statement on
     * the message will be exhaustive.
     */
    public val messages: SharedFlow<ClientMessageWrapper> = _messages.asSharedFlow()

    init {
        // needs to be done because of derived class initialization order
        emissionJob.start()
    }

    override suspend fun sendMessage(newMessage: ClientMessageWrapper): Boolean {
        _messages.emit(newMessage)
        return true
    }

    override fun onCompletion() {}
}
