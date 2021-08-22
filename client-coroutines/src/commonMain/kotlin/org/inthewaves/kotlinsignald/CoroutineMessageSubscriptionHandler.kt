package org.inthewaves.kotlinsignald

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.inthewaves.kotlinsignald.clientprotocol.SignaldException
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ClientMessageWrapper

/**
 * @param signal The [Signal] instance. Must be associated with an account registered with signald.
 * @param extraBufferCapacity Size of the buffer for emissions to the messages shared flow, allowing slow subscribers
 * to get values from the buffer without suspending emitters. The buffer space determines how much slow subscribers can
 * lag from the fast ones. (optional, cannot be negative, defaults to 20)
 */
public class CoroutineMessageSubscriptionHandler(
    signal: Signal,
    extraBufferCapacity: Int = 20,
    private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob())
) : MessageSubscriptionHandler(signal) {
    private val _messages = MutableSharedFlow<ClientMessageWrapper>(extraBufferCapacity = extraBufferCapacity)
    public val messages: SharedFlow<ClientMessageWrapper> = _messages.asSharedFlow()

    init {
        coroutineScope.launch {
            val iterator = subscription.initialMessages.listIterator()
            for (msg in iterator) {
                _messages.emit(msg)
                iterator.remove()
            }

            while (isActive) {
                val newMessage: ClientMessageWrapper? = try {
                    subscription.nextMessage()
                } catch (e: SignaldException) {
                    continue
                }
                if (newMessage == null) {
                    cancel("Message receive socket is closed --- no more incoming messages")
                    awaitCancellation()
                }

                _messages.emit(newMessage)
            }
        }
    }

    override fun close() {
        super.close()
        coroutineScope.cancel(message = "Closing the message subscription handler")
    }
}
