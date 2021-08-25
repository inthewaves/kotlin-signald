package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v0.structures.Success

@Serializable
public data class JsonSendMessageResult(
    public val address: JsonAddress? = null,
    public val success: Success? = null,
    /**
     * Example: false
     */
    public val networkFailure: Boolean? = null,
    /**
     * Example: false
     */
    public val unregisteredFailure: Boolean? = null,
    public val identityFailure: String? = null
)
