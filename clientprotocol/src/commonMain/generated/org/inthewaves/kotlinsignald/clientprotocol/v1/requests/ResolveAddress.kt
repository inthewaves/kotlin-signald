package org.inthewaves.kotlinsignald.clientprotocol.v1.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonAddress

/**
 * This class only represents the response from signald for the request. Make a request by creating
 * an instance of [org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ResolveAddressRequest] and
 * then calling its `submit` function.
 */
@Serializable
@SerialName("resolve_address")
public data class ResolveAddress private constructor(
    public override val `data`: JsonAddress? = null
) : JsonMessageWrapper<JsonAddress>()
