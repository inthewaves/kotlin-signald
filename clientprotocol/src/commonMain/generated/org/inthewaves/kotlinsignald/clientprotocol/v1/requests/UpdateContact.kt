package org.inthewaves.kotlinsignald.clientprotocol.v1.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.Profile

/**
 * This class only represents the response from signald for the request. Make a request by creating
 * an instance of [org.inthewaves.kotlinsignald.clientprotocol.v1.structures.UpdateContactRequest] and
 * then calling its `submit` function.
 */
@Serializable
@SerialName("update_contact")
internal data class UpdateContact private constructor(
    public override val data: Profile? = null
) : JsonMessageWrapper<Profile>()
