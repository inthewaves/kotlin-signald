package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
public data class JsonVerifiedMessage(
    public val destination: JsonAddress? = null,
    public val identityKey: String? = null,
    public val verified: String? = null,
    public val timestamp: Long? = null
)
