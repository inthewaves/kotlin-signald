package org.inthewaves.kotlinsignald.clientprotocol.v1.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GroupList

/**
 * This class only represents the response from signald for the request. Make a request by creating
 * an instance of [org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ListGroupsRequest] and
 * then calling its `submit` function.
 */
@Serializable
@SerialName("list_groups")
public data class ListGroups private constructor(
    public override val `data`: GroupList? = null
) : JsonMessageWrapper<GroupList>()
