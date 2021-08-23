package org.inthewaves.kotlinsignald

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException
import org.inthewaves.kotlinsignald.clientprotocol.SignaldException
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ClientMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ExceptionWrapper

/**
 * Creates a coroutine-based message handler. Messages can be received by collecting from the [messages] flow.
 * Unsubscription and closing of the socket is handled by calling [close] or cancelling the given [coroutineScope].
 *
 * Note that the [messages] flow is hot.
 *
 * @param signal The [Signal] instance. Must be associated with an account registered with signald.
 * @param extraBufferCapacity Size of the buffer for emissions to the messages shared flow, allowing slow subscribers
 * to get values from the buffer without suspending emitters. The buffer space determines how much slow subscribers can
 * lag from the fast ones. (optional, cannot be negative, defaults to 25)
 * @param coroutineScope The [CoroutineScope] to use for the message subscription coroutine.
 */
public class CoroutineMessageSubscriptionHandler(
    signal: Signal,
    private val coroutineScope: CoroutineScope,
    extraBufferCapacity: Int = 25,
) : MessageSubscriptionHandler(signal) {

    private val _messages = MutableSharedFlow<ClientMessageWrapper>(extraBufferCapacity = extraBufferCapacity)

    /**
     * A hot [SharedFlow] of incoming messages. As [ClientMessageWrapper] is a sealed type, using a `when` statement on
     * the message will be exhaustive.
     */
    public val messages: SharedFlow<ClientMessageWrapper> = _messages.asSharedFlow()

    init {
        coroutineScope.launch {
            while (isActive) {
                val newMessage: ClientMessageWrapper? = try {
                    subscription.nextMessage()
                } catch (e: SignaldException) {
                    val exceptionMessage = if (e is RequestFailedException && e.responseJsonString != null) {
                        "unable to serialize an incoming message " +
                            "(exceptionName = ${e::class.simpleName}, message = ${e.message}): " +
                            "${e.responseJsonString}"
                    } else {
                        "unable to serialize an incoming message " +
                            "(exceptionName = ${e::class.simpleName}, message = ${e.message})"
                    }
                    _messages.emit(
                        ExceptionWrapper(
                            data = ExceptionWrapper.Data(message = exceptionMessage, unexpected = true)
                        )
                    )
                    continue
                }
                if (newMessage == null) {
                    cancel("Message receive socket is closed --- no more incoming messages")
                    awaitCancellation()
                }

                _messages.emit(newMessage)
            }
        }.invokeOnCompletion { super.close() }
    }

    override fun close() {
        super.close()
        coroutineScope.cancel(message = "Closing the message subscription handler")
    }
}
