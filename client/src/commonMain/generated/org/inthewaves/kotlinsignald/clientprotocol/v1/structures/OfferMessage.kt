package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

@Serializable
public data class OfferMessage(
    public val id: Long? = null,
    public val sdp: String? = null,
    public val type: String? = null,
    public val opaque: String? = null
)
