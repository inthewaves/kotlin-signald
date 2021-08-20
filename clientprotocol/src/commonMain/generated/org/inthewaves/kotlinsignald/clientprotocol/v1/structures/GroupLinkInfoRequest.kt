package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.GroupLinkInfo
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 *
 * Get information about a group from a signal.group link
 */
@Serializable
@SerialName("group_link_info")
public data class GroupLinkInfoRequest(
    /**
     * The account to use
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * the signald.group link
     * Example:
     * "https://signal.group/#CjQKINH_GZhXhfifTcnBkaKTNRxW-hHKnGSq-cJNyPVqHRp8EhDUB7zjKNEl0NaULhsqJCX3"
     */
    public val uri: String
) : SignaldRequestBodyV1<GroupLinkInfo, JsonGroupJoinInfo>() {
    protected override val responseWrapperSerializer: KSerializer<GroupLinkInfo>
        get() = GroupLinkInfo.serializer()

    protected override val responseDataSerializer: KSerializer<JsonGroupJoinInfo>
        get() = JsonGroupJoinInfo.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        JsonGroupJoinInfo? = if (responseWrapper is GroupLinkInfo && responseWrapper.data is
        JsonGroupJoinInfo
    ) {
        responseWrapper.data
    } else {
        null
    }
}
