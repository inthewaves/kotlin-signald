package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

@Serializable
public data class JsonMention(
    /**
     * The UUID of the account being mentioned
     *
     * Example: "aeed01f0-a234-478e-8cf7-261c283151e7"
     */
    public val uuid: String? = null,
    /**
     * The number of characters in that the mention starts at. Note that due to a quirk of how
     * signald encodes JSON, if this value is 0 (for example if the first character in the message is
     * the mention) the field won't show up.
     *
     * Example: 4
     */
    public val start: Int? = null,
    /**
     * The length of the mention represented in the message. Seems to always be 1 but included here
     * in case that changes.
     *
     * Example: 1
     */
    public val length: Int? = null
)
