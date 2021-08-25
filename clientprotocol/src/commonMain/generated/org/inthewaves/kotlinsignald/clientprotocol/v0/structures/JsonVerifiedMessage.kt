package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.Serializable

@Serializable
@Deprecated("Will be removed after Sat, 1 Jan 2022 09:01:01 GMT")
public data class JsonVerifiedMessage(
    public val destination: JsonAddress? = null,
    public val identityKey: String? = null,
    public val verified: String? = null,
    public val timestamp: Long? = null
)
