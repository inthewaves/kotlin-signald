package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.Serializable

@Serializable
public data class OfferMessage(
    public val id: Long? = null,
    public val sdp: String? = null,
    public val type: Type? = null,
    public val opaque: String? = null
)
