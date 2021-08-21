package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class RemoteDelete(
    @SerialName("target_sent_timestamp")
    public val targetSentTimestamp: Long? = null
)
