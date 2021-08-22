package org.inthewaves.kotlinsignald.clientprotocol.v1.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ProfileList

/**
 * This class only represents the response from signald for the request. Make a request by creating
 * an instance of [org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ListContactsRequest] and
 * then calling its `submit` function.
 */
@Serializable
@SerialName("list_contacts")
public data class ListContacts private constructor(
    public override val data: ProfileList? = null
) : JsonMessageWrapper<ProfileList>()
