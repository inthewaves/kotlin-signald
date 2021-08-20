package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.Serializable

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
public data class ConfigurationMessage(
    public val readReceipts: Optional? = null,
    public val unidentifiedDeliveryIndicators: Optional? = null,
    public val typingIndicators: Optional? = null,
    public val linkPreviews: Optional? = null
)
