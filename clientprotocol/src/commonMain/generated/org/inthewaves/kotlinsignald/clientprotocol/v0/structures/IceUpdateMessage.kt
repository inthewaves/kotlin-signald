package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.Serializable

@Serializable
public data class IceUpdateMessage(
    public val id: Long? = null,
    public val opaque: String? = null,
    public val sdp: String? = null
)
