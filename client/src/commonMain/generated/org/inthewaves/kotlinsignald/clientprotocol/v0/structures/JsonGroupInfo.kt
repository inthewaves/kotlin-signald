package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.Serializable

@Serializable
@Deprecated("Will be removed after Sat, 1 Jan 2022 09:01:01 GMT")
public data class JsonGroupInfo(
    public val groupId: String? = null,
    public val members: List<JsonAddress> = emptyList(),
    public val name: String? = null,
    public val type: String? = null,
    public val avatarId: Long? = null
)
