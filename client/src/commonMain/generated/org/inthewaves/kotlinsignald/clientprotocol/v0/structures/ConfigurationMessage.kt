package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.Serializable

@Serializable
public data class ConfigurationMessage(
    public val readReceipts: Optional? = null,
    public val unidentifiedDeliveryIndicators: Optional? = null,
    public val typingIndicators: Optional? = null,
    public val linkPreviews: Optional? = null
)
