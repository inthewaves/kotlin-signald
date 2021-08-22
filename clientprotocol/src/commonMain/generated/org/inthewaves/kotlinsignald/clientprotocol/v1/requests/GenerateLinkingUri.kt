package org.inthewaves.kotlinsignald.clientprotocol.v1.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.LinkingURI

/**
 * This class only represents the response from signald for the request. Make a request by creating
 * an instance of [org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GenerateLinkingURIRequest]
 * and then calling its `submit` function.
 */
@Serializable
@SerialName("generate_linking_uri")
public data class GenerateLinkingUri private constructor(
    public override val data: LinkingURI? = null
) : JsonMessageWrapper<LinkingURI>()
