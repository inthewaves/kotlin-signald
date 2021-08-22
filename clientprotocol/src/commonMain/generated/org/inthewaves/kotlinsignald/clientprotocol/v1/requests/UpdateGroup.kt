package org.inthewaves.kotlinsignald.clientprotocol.v1.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GroupInfo

/**
 * This class only represents the response from signald for the request. Make a request by creating
 * an instance of [org.inthewaves.kotlinsignald.clientprotocol.v1.structures.UpdateGroupRequest] and
 * then calling its `submit` function.
 */
@Serializable
@SerialName("update_group")
public data class UpdateGroup private constructor(
    public override val data: GroupInfo? = null
) : JsonMessageWrapper<GroupInfo>()
