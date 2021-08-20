package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 *
 * information about a legacy group
 */
@Serializable
public data class JsonGroupInfo(
    public val groupId: String? = null,
    public val members: List<JsonAddress> = emptyList(),
    public val name: String? = null,
    public val type: String? = null,
    public val avatarId: Long? = null
)
