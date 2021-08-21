package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.Serializable

@Serializable
@Deprecated("Will be removed on Sat, 1 Jan 2022 09:01:01 GMT")
public data class JsonMessageEnvelope(
    /**
     * Example: "+12024561414"
     */
    public val username: String? = null,
    /**
     * Example: "0cc10e61-d64c-4dbc-b51c-334f7dd45a4a"
     */
    public val uuid: String? = null,
    public val source: JsonAddress? = null,
    public val sourceDevice: Int? = null,
    public val type: String? = null,
    public val relay: String? = null,
    /**
     * Example: 1615576442475
     */
    public val timestamp: Long? = null,
    public val timestampISO: String? = null,
    public val serverTimestamp: Long? = null,
    /**
     * Example: 161557644247580
     */
    public val serverDeliveredTimestamp: Long? = null,
    public val hasLegacyMessage: Boolean? = null,
    public val hasContent: Boolean? = null,
    public val isUnidentifiedSender: Boolean? = null,
    public val dataMessage: JsonDataMessage? = null,
    public val syncMessage: JsonSyncMessage? = null,
    public val callMessage: JsonCallMessage? = null,
    public val receipt: JsonReceiptMessage? = null,
    public val typing: JsonTypingMessage? = null
)
