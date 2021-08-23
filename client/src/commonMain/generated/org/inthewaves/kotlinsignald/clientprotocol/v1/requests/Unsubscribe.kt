package org.inthewaves.kotlinsignald.clientprotocol.v1.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.SubscriptionResponse

/**
 * This class only represents the response from signald for the request. Make a request by creating
 * an instance of [org.inthewaves.kotlinsignald.clientprotocol.v1.structures.UnsubscribeRequest] and
 * then calling its `submit` function.
 */
@Serializable
@SerialName("unsubscribe")
internal data class Unsubscribe private constructor(
    public override val data: SubscriptionResponse? = null
) : JsonMessageWrapper<SubscriptionResponse>()
