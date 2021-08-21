package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

@Serializable
public data class JsonSentTranscriptMessage(
    public val destination: JsonAddress? = null,
    /**
     * Example: 1615576442475
     */
    public val timestamp: Long? = null,
    public val expirationStartTimestamp: Long? = null,
    public val message: JsonDataMessage? = null,
    public val unidentifiedStatus: Map<String, Boolean>? = null,
    public val isRecipientUpdate: Boolean? = null
)
