package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.Serializable

@Serializable
public data class RemoteDelete(
    public val targetSentTimestamp: Long? = null
)
