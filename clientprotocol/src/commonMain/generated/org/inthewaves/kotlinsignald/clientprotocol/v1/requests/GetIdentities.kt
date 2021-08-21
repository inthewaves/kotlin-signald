package org.inthewaves.kotlinsignald.clientprotocol.v1.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.IdentityKeyList

/**
 * This class only represents the response from signald for the request. Make a request by creating
 * an instance of [org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GetIdentitiesRequest] and
 * then calling its `submit` function.
 */
@Serializable
@SerialName("get_identities")
public data class GetIdentities private constructor(
    public override val `data`: IdentityKeyList? = null
) : JsonMessageWrapper<IdentityKeyList>()
