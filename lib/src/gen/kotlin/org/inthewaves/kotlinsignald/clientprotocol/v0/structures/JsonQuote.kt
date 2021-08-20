package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.Serializable

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 *
 * A quote is a reply to a previous message. ID is the sent time of the message being replied to
 */
@Serializable
@Deprecated("Will be removed on Sat, 1 Jan 2022 09:01:01 GMT")
public data class JsonQuote(
    /**
     * the client timestamp of the message being quoted
     * Example: 1615576442475
     */
    public val id: Long? = null,
    /**
     * the author of the message being quoted
     */
    public val author: JsonAddress? = null,
    /**
     * the body of the message being quoted
     * Example: "hey ￼ what's up?"
     */
    public val text: String? = null,
    /**
     * list of files attached to the quoted message
     */
    public val attachments: List<JsonQuotedAttachment> = emptyList(),
    /**
     * list of mentions in the quoted message
     */
    public val mentions: List<JsonMention> = emptyList()
)
