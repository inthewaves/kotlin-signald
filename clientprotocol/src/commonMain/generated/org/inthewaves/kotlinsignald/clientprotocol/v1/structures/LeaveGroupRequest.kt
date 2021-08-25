package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.LeaveGroup

@Serializable
@SerialName("leave_group")
public data class LeaveGroupRequest(
    /**
     * The account to use
     *
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * The group to leave
     *
     * Example: "EdSqI90cS0UomDpgUXOlCoObWvQOXlH5G3Z2d3f4ayE="
     */
    public val groupID: String
) : SignaldRequestBodyV1<GroupInfo>() {
    internal override val responseWrapperSerializer: KSerializer<LeaveGroup>
        get() = LeaveGroup.serializer()

    internal override val responseDataSerializer: KSerializer<GroupInfo>
        get() = GroupInfo.serializer()

    internal override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>): GroupInfo? =
        if (responseWrapper is LeaveGroup && responseWrapper.data is GroupInfo) {
            responseWrapper.data
        } else {
            null
        }
}
