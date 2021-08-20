package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 *
 * Responses from the subscribe / unsubscribe endpoint. The protocol describes these as empty
 * responses, but race conditions can occur. This response can contain messages sent before the
 * (un)subscribe acknowledgement message from signald.
 */
@Serializable
public data class SubscriptionResponse(
    /**
     * Messages that have been sent by the socket during the (un)subscribe submission.
     */
    public val messages: List<ClientMessageWrapper> = emptyList()
) : SignaldResponseBodyV1()
