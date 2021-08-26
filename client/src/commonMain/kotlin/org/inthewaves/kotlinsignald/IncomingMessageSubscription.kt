package org.inthewaves.kotlinsignald

import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ClientMessageWrapper

/**
 * Represents an active incoming message subscription with signald.
 *
 * This interface is here because JavaScript has a different client implementation based on suspending functions, while
 * all the other platforms use non-suspend functions.
 */
public interface IncomingMessageSubscription {
    /**
     * The number of messages sent while we were waiting for signald's response to the subscribe request.
     * This is currently used for detecting such a situation.
     */
    public val initialMessagesCount: Int

    public fun close()

    public suspend fun nextMessageSuspend(): ClientMessageWrapper?
}
