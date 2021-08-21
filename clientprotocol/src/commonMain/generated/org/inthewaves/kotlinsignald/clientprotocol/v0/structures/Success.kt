package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.Serializable

@Serializable
public data class Success(
    public val unidentified: Boolean? = null,
    public val needsSync: Boolean? = null,
    public val duration: Long? = null
)
