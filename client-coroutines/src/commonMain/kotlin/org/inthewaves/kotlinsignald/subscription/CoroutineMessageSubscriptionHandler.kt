package org.inthewaves.kotlinsignald.subscription

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.inthewaves.kotlinsignald.IncomingMessageSubscription
import org.inthewaves.kotlinsignald.SignaldClient
import org.inthewaves.kotlinsignald.clientprotocol.AutoCloseable
import org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException
import org.inthewaves.kotlinsignald.clientprotocol.SignaldException
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ClientMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ExceptionWrapper
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

internal expect suspend fun getSubscription(signaldClient: SignaldClient): IncomingMessageSubscription

internal expect suspend fun getNextMessage(subscription: IncomingMessageSubscription): ClientMessageWrapper?

/**
 * An abstract incoming message subscription handler that uses a coroutine to handle the event loop for messages.
 *
 * The coroutine is the [emissionJob], which is started lazily. **Derived classes must start the [emissionJob]
 * explicitly by calling `emissionJob.start()` in an init block to ensure that asynchronous work is only ready when the
 * derived class is ready**. This is because of
 * [derived class initialization order](https://kotlinlang.org/docs/inheritance.html#derived-class-initialization-order).
 * Read more at [https://www.ibm.com/developerworks/java/library/j-jtp0618/index.html].
 *
 * @param signaldClient The [SignaldClient] instance. Must be associated with an account registered with signald.
 * @param coroutineScope The [CoroutineScope] to use for the message subscription coroutine. This is used to cancel
 * the handler.
 * @param context Additional coroutine context to the `coroutineScope`'s context.
 */
public abstract class CoroutineMessageSubscriptionHandler(
    protected val signaldClient: SignaldClient,
    coroutineScope: CoroutineScope,
    context: CoroutineContext = EmptyCoroutineContext,
) : AutoCloseable {
    private var subscription: IncomingMessageSubscription? = null

    protected val emissionJob: Job = coroutineScope.launch(context, start = CoroutineStart.LAZY) {
        val subscription = getSubscription(signaldClient).also { subscription = it }
        while (isActive) {
            val newMessage: ClientMessageWrapper = try {
                getNextMessage(subscription)
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
            sendMessageToSubscribers(newMessage)
        }
    }.also { job ->
        job.invokeOnCompletion {
            subscription?.close()
            onCompletion()
        }
    }

    /*
    // Derived classes need to call this after they're ready because of derived class initialization order.
    // The base class will be initialized first.

    init {
        emissionJob.start()
    }
     */

    /**
     * Sends a message to subscribers and returns whether sending was successful. If false is returned, the
     * emission job will finish normally.
     */
    protected abstract suspend fun sendMessageToSubscribers(newMessage: ClientMessageWrapper): Boolean

    /**
     * A handler function that is synchronously invoked on completion of the message emission job.
     * This should not throw any exceptions.
     *
     * @see [Job.invokeOnCompletion]
     */
    protected abstract fun onCompletion()

    public override fun close() {
        subscription?.close()
        emissionJob.cancel(message = "Closing the message subscription handler")
    }
}
