package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.Serializable

@Serializable
@Deprecated("Will be removed after Sat, 1 Jan 2022 09:01:01 GMT")
public data class JsonReadMessage(
    public val sender: JsonAddress? = null,
    /**
     * Example: 1615576442475
     */
    public val timestamp: Long? = null
)
