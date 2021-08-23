package org.inthewaves.kotlinsignald.clientprotocol.v1.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This class only represents the response from signald for the request. Make a request by creating
 * an instance of [org.inthewaves.kotlinsignald.clientprotocol.v1.structures.AddServerRequest] and then
 * calling its `submit` function.
 */
@Serializable
@SerialName("add_server")
internal data class AddServer private constructor(
    public override val data: String? = null
) : JsonMessageWrapper<String>()
