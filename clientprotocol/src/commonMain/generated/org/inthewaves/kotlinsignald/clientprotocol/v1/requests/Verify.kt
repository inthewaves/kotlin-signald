package org.inthewaves.kotlinsignald.clientprotocol.v1.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.Account

/**
 * This class only represents the response from signald for the request. Make a request by creating
 * an instance of [org.inthewaves.kotlinsignald.clientprotocol.v1.structures.VerifyRequest] and then
 * calling its `submit` function.
 */
@Serializable
@SerialName("verify")
internal data class Verify private constructor(
    public override val data: Account? = null
) : JsonMessageWrapper<Account>()
