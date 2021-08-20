package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.UpdateGroup

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 *
 * modify a group. Note that only one modification action may be preformed at once
 */
@Serializable
@SerialName("update_group")
public data class UpdateGroupRequest(
    /**
     * The identifier of the account to interact with
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * the ID of the group to update
     * Example: "EdSqI90cS0UomDpgUXOlCoObWvQOXlH5G3Z2d3f4ayE="
     */
    public val groupID: String,
    /**
     * Example: "Parkdale Run Club"
     */
    public val title: String? = null,
    /**
     * Example: "/tmp/image.jpg"
     */
    public val avatar: String? = null,
    /**
     * update the group timer.
     */
    public val updateTimer: Int? = null,
    public val addMembers: List<JsonAddress> = emptyList(),
    public val removeMembers: List<JsonAddress> = emptyList(),
    public val updateRole: GroupMember? = null,
    /**
     * note that only one of the access controls may be updated per request
     */
    public val updateAccessControl: GroupAccessControl? = null,
    /**
     * regenerate the group link password, invalidating the old one
     */
    public val resetLink: Boolean? = null
) : SignaldRequestBodyV1<UpdateGroup, GroupInfo>() {
    protected override val responseWrapperSerializer: KSerializer<UpdateGroup>
        get() = UpdateGroup.serializer()

    protected override val responseDataSerializer: KSerializer<GroupInfo>
        get() = GroupInfo.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>): GroupInfo? =
        if (responseWrapper is UpdateGroup && responseWrapper.data is GroupInfo) {
            responseWrapper.data
        } else {
            null
        }
}
