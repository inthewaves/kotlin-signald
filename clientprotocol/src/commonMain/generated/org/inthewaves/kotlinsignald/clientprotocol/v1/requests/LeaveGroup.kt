package org.inthewaves.kotlinsignald.clientprotocol.v1.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GroupInfo

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 *
 * This class only represents the response from signald for the request. Make a request by creating
 * an instance of [org.inthewaves.kotlinsignald.clientprotocol.v1.structures.LeaveGroupRequest] and
 * then calling its `submit` function.
 */
@Serializable
@SerialName("leave_group")
public data class LeaveGroup private constructor(
    public override val data: GroupInfo? = null
) : JsonMessageWrapper<GroupInfo>()
