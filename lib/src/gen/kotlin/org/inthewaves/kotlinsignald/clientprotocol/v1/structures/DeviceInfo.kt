package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
public data class DeviceInfo(
    public val id: Long? = null,
    public val name: String? = null,
    public val created: Long? = null,
    public val lastSeen: Long? = null
)
