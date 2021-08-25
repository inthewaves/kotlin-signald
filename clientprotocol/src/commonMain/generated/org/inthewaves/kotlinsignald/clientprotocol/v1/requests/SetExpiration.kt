package org.inthewaves.kotlinsignald.clientprotocol.v1.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.SendResponse

/**
 * This class only represents the response from signald for the request. Make a request by creating
 * an instance of [org.inthewaves.kotlinsignald.clientprotocol.v1.structures.SetExpirationRequest] and
 * then calling its `submit` function.
 */
@Serializable
@SerialName("set_expiration")
internal data class SetExpiration private constructor(
    public override val data: SendResponse? = null
) : JsonMessageWrapper<SendResponse>()
