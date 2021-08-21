package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v0.structures.ConfigurationMessage
import org.inthewaves.kotlinsignald.clientprotocol.v0.structures.JsonAttachment
import org.inthewaves.kotlinsignald.clientprotocol.v0.structures.JsonStickerPackOperationMessage

@Serializable
public data class JsonSyncMessage(
    public val sent: JsonSentTranscriptMessage? = null,
    public val contacts: JsonAttachment? = null,
    public val contactsComplete: Boolean? = null,
    public val groups: JsonAttachment? = null,
    public val blockedList: JsonBlockedListMessage? = null,
    public val request: String? = null,
    public val readMessages: List<JsonReadMessage> = emptyList(),
    public val viewOnceOpen: JsonViewOnceOpenMessage? = null,
    public val verified: JsonVerifiedMessage? = null,
    public val configuration: ConfigurationMessage? = null,
    public val stickerPackOperations: List<JsonStickerPackOperationMessage> = emptyList(),
    public val fetchType: String? = null,
    public val messageRequestResponse: JsonMessageRequestResponseMessage? = null
)
