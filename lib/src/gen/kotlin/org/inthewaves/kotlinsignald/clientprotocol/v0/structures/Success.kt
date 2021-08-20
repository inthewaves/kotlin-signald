package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.Serializable

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
public data class Success(
    public val unidentified: Boolean? = null,
    public val needsSync: Boolean? = null,
    public val duration: Long? = null
)
