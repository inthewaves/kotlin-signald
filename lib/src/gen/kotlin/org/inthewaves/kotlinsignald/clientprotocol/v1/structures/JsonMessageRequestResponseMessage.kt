package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
public data class JsonMessageRequestResponseMessage(
    public val person: JsonAddress? = null,
    public val groupId: String? = null,
    public val type: String? = null
)
