package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
public data class JsonReadMessage(
    public val sender: JsonAddress? = null,
    /**
     * Example: 1615576442475
     */
    public val timestamp: Long? = null
)
