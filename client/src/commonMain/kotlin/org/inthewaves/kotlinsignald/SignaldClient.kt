package org.inthewaves.kotlinsignald

/**
 * Represents a client able to communicate with a signald socket.
 *
 * This interface is here because JavaScript has a different client implementation based on suspending functions, while
 * all the other platforms use non-suspend functions.
 */
public interface SignaldClient {
    public suspend fun subscribeSuspend(): IncomingMessageSubscription
}