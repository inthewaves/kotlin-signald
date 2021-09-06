// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.UpdateGroup

/**
 * modify a group. Note that only one modification action may be performed at once
 */
@Serializable
@SerialName("update_group")
public data class UpdateGroupRequest(
    /**
     * The identifier of the account to interact with
     *
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * the ID of the group to update
     *
     * Example: "EdSqI90cS0UomDpgUXOlCoObWvQOXlH5G3Z2d3f4ayE="
     */
    public val groupID: String,
    /**
     * Example: "Parkdale Run Club"
     */
    public val title: String? = null,
    /**
     * A new group description. Set to empty string to remove an existing description.
     *
     * Example: "A club for running in Parkdale"
     */
    public val description: String? = null,
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
) : SignaldRequestBodyV1<GroupInfo>() {
    internal override val responseWrapperSerializer: KSerializer<UpdateGroup>
        get() = UpdateGroup.serializer()

    internal override val responseDataSerializer: KSerializer<GroupInfo>
        get() = GroupInfo.serializer()

    internal override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>): GroupInfo? =
        if (responseWrapper is UpdateGroup && responseWrapper.data is GroupInfo) {
            responseWrapper.data
        } else {
            null
        }
}