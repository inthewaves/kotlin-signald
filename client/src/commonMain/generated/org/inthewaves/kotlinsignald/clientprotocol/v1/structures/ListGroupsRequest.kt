package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.ListGroups

@Serializable
@SerialName("list_groups")
public data class ListGroupsRequest(
    public val account: String
) : SignaldRequestBodyV1<GroupList>() {
    internal override val responseWrapperSerializer: KSerializer<ListGroups>
        get() = ListGroups.serializer()

    internal override val responseDataSerializer: KSerializer<GroupList>
        get() = GroupList.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>): GroupList? =
        if (responseWrapper is ListGroups && responseWrapper.data is GroupList) {
            responseWrapper.data
        } else {
            null
        }
}
