package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.Serializable

@Serializable
@Deprecated("Will be removed on Sat, 1 Jan 2022 09:01:01 GMT")
public data class JsonMessageRequestResponseMessage(
    public val person: JsonAddress? = null,
    public val groupId: String? = null,
    public val type: String? = null
)
