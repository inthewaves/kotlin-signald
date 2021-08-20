package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JoinGroup
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 *
 * Join a group using the a signal.group URL. Note that you must have a profile name set to join
 * groups.
 */
@Serializable
@SerialName("join_group")
public data class JoinGroupRequest(
    /**
     * The account to interact with
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * The signal.group URL
     * Example:
     * "https://signal.group/#CjQKINH_GZhXhfifTcnBkaKTNRxW-hHKnGSq-cJNyPVqHRp8EhDUB7zjKNEl0NaULhsqJCX3"
     */
    public val uri: String
) : SignaldRequestBodyV1<JoinGroup, JsonGroupJoinInfo>() {
    protected override val responseWrapperSerializer: KSerializer<JoinGroup>
        get() = JoinGroup.serializer()

    protected override val responseDataSerializer: KSerializer<JsonGroupJoinInfo>
        get() = JsonGroupJoinInfo.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        JsonGroupJoinInfo? = if (responseWrapper is JoinGroup && responseWrapper.data is
        JsonGroupJoinInfo
    ) {
        responseWrapper.data
    } else {
        null
    }
}
