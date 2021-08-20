package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.serializers.UUIDSerializer
import java.util.UUID

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
@Deprecated("Will be removed on Sat, 1 Jan 2022 09:01:01 GMT")
public data class JsonAccount(
    public val deviceId: Int? = null,
    public val username: String? = null,
    public val filename: String? = null,
    @Serializable(UUIDSerializer::class)
    public val uuid: UUID? = null,
    public val registered: Boolean? = null,
    @SerialName("has_keys")
    public val hasKeys: Boolean? = null,
    public val subscribed: Boolean? = null
)
