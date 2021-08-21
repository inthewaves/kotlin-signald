package org.inthewaves.kotlinsignald.clientprotocol.v1.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonGroupV2Info

/**
 * This class only represents the response from signald for the request. Make a request by creating
 * an instance of [org.inthewaves.kotlinsignald.clientprotocol.v1.structures.CreateGroupRequest] and
 * then calling its `submit` function.
 */
@Serializable
@SerialName("create_group")
public data class CreateGroup private constructor(
    public override val `data`: JsonGroupV2Info? = null
) : JsonMessageWrapper<JsonGroupV2Info>()
