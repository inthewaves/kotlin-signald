package org.inthewaves.kotlinsignald.subscription

import org.inthewaves.kotlinsignald.Signal
import org.inthewaves.kotlinsignald.clientprotocol.SignaldException
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ClientMessageWrapper

/**
 * A message subscription handler that uses the current thread to consume messages forever (until error such as the
 * socket reaching EOF).
 */
public class BlockingMessageSubscriptionHandler(signal: Signal) : MessageSubscriptionHandler(signal) {
    /**
     * Consumes every incoming message. Execution stops normally if the socket reaches EOF; otherwise, an exception
     * is thrown. After this function executes, the socket will not be closed; [close] has to be called manually.
     *
     * @throws SignaldException if an I/O error occurs when communicating with the socket
     */
    @Throws(SignaldException::class)
    public fun consumeEach(messageConsumer: (ClientMessageWrapper) -> Unit) {
        var nextMessage = subscription.nextMessage()
        while (nextMessage != null) {
            messageConsumer(nextMessage)
            nextMessage = subscription.nextMessage()
        }
    }
}
