package org.inthewaves.kotlinsignald

import org.inthewaves.kotlinsignald.clientprotocol.SignaldException

/**
 * A handler to handle subscriptions from signald.
 *
 * @constructor Creates a new handler by using the [signal] instance to subscribe via [Signal.subscribe].
 * @throws SignaldException if subscription fails (e.g., creating the persistent socket fails)
 */
public abstract class MessageSubscriptionHandler @Throws(SignaldException::class) constructor(
    public val signal: Signal
) {
    protected val subscription: Signal.Subscription = signal.subscribe()

    public open fun close() {
        subscription.unsubscribe()
        subscription.persistentSocket.close()
    }
}
