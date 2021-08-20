package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v0.structures.JsonCallMessage
import org.inthewaves.kotlinsignald.clientprotocol.v0.structures.JsonReceiptMessage
import org.inthewaves.kotlinsignald.clientprotocol.v0.structures.JsonTypingMessage
import org.inthewaves.kotlinsignald.serializers.UUIDSerializer
import java.util.UUID

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
public data class JsonMessageEnvelope(
    /**
     * Example: "+12024561414"
     */
    public val username: String? = null,
    /**
     * Example: "0cc10e61-d64c-4dbc-b51c-334f7dd45a4a"
     */
    @Serializable(UUIDSerializer::class)
    public val uuid: UUID? = null,
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
