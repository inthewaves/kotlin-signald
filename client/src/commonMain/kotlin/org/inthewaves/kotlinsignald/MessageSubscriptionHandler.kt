package org.inthewaves.kotlinsignald

import org.inthewaves.kotlinsignald.clientprotocol.SignaldException

/**
 * A handler to handle incoming message subscriptions from a [Signal.Subscription].
 *
 * @constructor Creates a new handler by using the [signal] instance to subscribe via [Signal.subscribe].
 * @throws SignaldException if subscription fails (e.g., creating the persistent socket fails)
 */
public abstract class MessageSubscriptionHandler @Throws(SignaldException::class) constructor(signal: Signal) {
    protected val subscription: Signal.Subscription = signal.subscribe()

    /**
     * The number of messages sent while we were waiting for signald's response to the subscribe request.
     * This is currently used for detecting such a situation.
     */
    public val initialMessagesCount: Int = subscription.initialMessagesCount

    public open fun close() {
        subscription.unsubscribe()
        subscription.close()
    }
}
