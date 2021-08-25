package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.Serializable

@Serializable
@Deprecated("Will be removed after Sat, 1 Jan 2022 09:01:01 GMT")
public data class JsonReceiptMessage(
    public val type: String? = null,
    public val timestamps: List<Long> = emptyList(),
    public val `when`: Long? = null
)
