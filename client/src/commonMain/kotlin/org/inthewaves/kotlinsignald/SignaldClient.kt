package org.inthewaves.kotlinsignald

/**
 * Represents a client able to communicate with a signald socket.
 *
 * This interface is here because JavaScript has a different client implementation based on suspending functions, while
 * all the other platforms use non-suspend functions.
 */
public interface SignaldClient {
    /**
     * @throws [UnsupportedOperationException] if the client doesn't support it (JS)
     */
    public fun subscribe(): IncomingMessageSubscription
    public suspend fun subscribeSuspend(): IncomingMessageSubscription
}
