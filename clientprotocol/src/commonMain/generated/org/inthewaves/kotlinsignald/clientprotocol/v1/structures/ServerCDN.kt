package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

@Serializable
public data class ServerCDN(
    public val number: Int? = null,
    public val url: String? = null
)
