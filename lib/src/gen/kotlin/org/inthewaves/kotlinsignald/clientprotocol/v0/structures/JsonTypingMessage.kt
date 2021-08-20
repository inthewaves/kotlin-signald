package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.Serializable

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
@Deprecated("Will be removed on Sat, 1 Jan 2022 09:01:01 GMT")
public data class JsonTypingMessage(
    public val action: String? = null,
    public val timestamp: Long? = null,
    public val groupId: String? = null
)
