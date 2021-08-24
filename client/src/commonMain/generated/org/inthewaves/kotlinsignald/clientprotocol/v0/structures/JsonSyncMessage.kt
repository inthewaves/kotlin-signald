package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.Serializable

@Serializable
@Deprecated("Will be removed after Sat, 1 Jan 2022 09:01:01 GMT")
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
