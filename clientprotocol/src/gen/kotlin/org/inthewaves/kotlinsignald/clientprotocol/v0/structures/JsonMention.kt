package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.Serializable

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
@Deprecated("Will be removed on Sat, 1 Jan 2022 09:01:01 GMT")
public data class JsonMention(
    /**
     * The UUID of the account being mentioned
     * Example: "aeed01f0-a234-478e-8cf7-261c283151e7"
     */
    public val uuid: String? = null,
    /**
     * The number of characters in that the mention starts at. Note that due to a quirk of how
     * signald encodes JSON, if this value is 0 (for example if the first character in the message is
     * the mention) the field won't show up.
     * Example: 4
     */
    public val start: Int? = null,
    /**
     * The length of the mention represented in the message. Seems to always be 1 but included here
     * in case that changes.
     * Example: 1
     */
    public val length: Int? = null
)
