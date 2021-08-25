package org.inthewaves.kotlinsignald.clientprotocol.v1.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.AllIdentityKeyList

/**
 * This class only represents the response from signald for the request. Make a request by creating
 * an instance of [org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GetAllIdentities] and then
 * calling its `submit` function.
 */
@Serializable
@SerialName("get_all_identities")
internal data class GetAllIdentities private constructor(
    public override val data: AllIdentityKeyList? = null
) : JsonMessageWrapper<AllIdentityKeyList>()
