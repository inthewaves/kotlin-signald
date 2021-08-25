package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

@Serializable
public data class JsonReaction(
    /**
     * the emoji to react with
     *
     * Example: "👍"
     */
    public val emoji: String? = null,
    /**
     * set to true to remove the reaction. requires emoji be set to previously reacted emoji
     */
    public val remove: Boolean? = null,
    /**
     * the author of the message being reacted to
     */
    public val targetAuthor: JsonAddress? = null,
    /**
     * the client timestamp of the message being reacted to
     *
     * Example: 1615576442475
     */
    public val targetSentTimestamp: Long? = null
)
