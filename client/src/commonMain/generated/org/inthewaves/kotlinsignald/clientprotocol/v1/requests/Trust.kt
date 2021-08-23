package org.inthewaves.kotlinsignald.clientprotocol.v1.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.EmptyResponse

/**
 * This class only represents the response from signald for the request. Make a request by creating
 * an instance of [org.inthewaves.kotlinsignald.clientprotocol.v1.structures.TrustRequest] and then
 * calling its `submit` function.
 */
@Serializable
@SerialName("trust")
internal data class Trust private constructor(
    public override val data: EmptyResponse? = null
) : JsonMessageWrapper<EmptyResponse>()
