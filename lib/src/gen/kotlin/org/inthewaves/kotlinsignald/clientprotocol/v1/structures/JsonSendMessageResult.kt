package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v0.structures.Success

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
public data class JsonSendMessageResult(
    public val address: JsonAddress? = null,
    public val success: Success? = null,
    /**
     * Example: false
     */
    public val networkFailure: Boolean? = null,
    /**
     * Example: false
     */
    public val unregisteredFailure: Boolean? = null,
    public val identityFailure: String? = null
)
