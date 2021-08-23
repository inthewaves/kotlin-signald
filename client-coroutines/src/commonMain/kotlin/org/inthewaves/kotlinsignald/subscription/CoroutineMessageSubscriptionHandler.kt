package org.inthewaves.kotlinsignald.subscription

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.inthewaves.kotlinsignald.Signal
import org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException
import org.inthewaves.kotlinsignald.clientprotocol.SignaldException
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ClientMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ExceptionWrapper
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * @param signal The [Signal] instance. Must be associated with an account registered with signald.
 * @param coroutineScope The [CoroutineScope] to use for the message subscription coroutine. This is used to cancel
 * the handler.
 */
public abstract class CoroutineMessageSubscriptionHandler(
    signal: Signal,
    coroutineScope: CoroutineScope,
    context: CoroutineContext = EmptyCoroutineContext,
) : MessageSubscriptionHandler(signal) {

    protected val emissionJob: Job = coroutineScope.launch(context) {
        while (isActive) {
            val newMessage: ClientMessageWrapper = try {
                subscription.nextMessage()
                    ?: run {
                        cancel("Message receive socket is closed --- no more incoming messages")
                        awaitCancellation()
                    }
            } catch (e: SignaldException) {
                val exceptionMessage = if (e is RequestFailedException && e.responseJsonString != null) {
                    "unable to serialize an incoming message " +
                        "(exceptionName = ${e::class.simpleName}, message = ${e.message}): " +
                        "${e.responseJsonString}"
                } else {
                    "unable to serialize an incoming message " +
                        "(exceptionName = ${e::class.simpleName}, message = ${e.message})"
                }

                ExceptionWrapper(data = ExceptionWrapper.Data(message = exceptionMessage, unexpected = true))
            }
            if (!sendMessage(newMessage)) {
                break
            }
        }
    }.also {
        it.invokeOnCompletion {
            super.close()
            onCompletion()
        }
    }

    /**
     * Sends a message to subscribers and returns whether sending was successful. If false is returned, the
     * emission job will finish.
     */
    protected abstract suspend fun sendMessage(newMessage: ClientMessageWrapper): Boolean

    /**
     * A handler function that is synchronously invoked once on completion of the message emission job.
     */
    protected abstract fun onCompletion()

    override fun close() {
        super.close()
        emissionJob.cancel(message = "Closing the message subscription handler")
    }

    public companion object {
        internal const val DEFAULT_BUFFER_CAPACITY = 25
    }
}
