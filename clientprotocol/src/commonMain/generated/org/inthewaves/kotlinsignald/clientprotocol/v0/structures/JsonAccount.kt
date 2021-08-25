package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Deprecated("Will be removed after Sat, 1 Jan 2022 09:01:01 GMT")
public data class JsonAccount(
    public val deviceId: Int? = null,
    public val username: String? = null,
    public val filename: String? = null,
    public val uuid: String? = null,
    public val registered: Boolean? = null,
    @SerialName("has_keys")
    public val hasKeys: Boolean? = null,
    public val subscribed: Boolean? = null
)
