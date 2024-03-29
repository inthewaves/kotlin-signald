// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.SuspendSocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.GroupLinkInfo
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper

/**
 * Get information about a group from a signal.group link
 */
@Serializable
@SerialName("group_link_info")
public data class GroupLinkInfoRequest(
    /**
     * The account to use
     *
     * Example: "0cc10e61-d64c-4dbc-b51c-334f7dd45a4a"
     */
    public val account: String,
    /**
     * the signald.group link
     *
     * Example:
     * "https://signal.group/#CjQKINH_GZhXhfifTcnBkaKTNRxW-hHKnGSq-cJNyPVqHRp8EhDUB7zjKNEl0NaULhsqJCX3"
     */
    public val uri: String
) : SignaldRequestBodyV1<JsonGroupJoinInfo>() {
    internal override val responseWrapperSerializer: KSerializer<GroupLinkInfo>
        get() = GroupLinkInfo.serializer()

    internal override val responseDataSerializer: KSerializer<JsonGroupJoinInfo>
        get() = JsonGroupJoinInfo.serializer()

    internal override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        JsonGroupJoinInfo? = if (responseWrapper is GroupLinkInfo && responseWrapper.data is
        JsonGroupJoinInfo
    ) {
        responseWrapper.data
    } else {
        null
    }

    /**
     * @throws org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException if the signald
     * socket sends a bad or error response, or unable to serialize our request
     * @throws org.inthewaves.kotlinsignald.clientprotocol.SignaldException if an I/O error occurs
     * during socket communication
     * @throws GroupLinkNotActiveError
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     * @throws InvalidRequestError
     * @throws GroupVerificationError
     * @throws SQLError
     */
    public override fun submit(socketCommunicator: SocketCommunicator, id: String):
        JsonGroupJoinInfo = super.submit(socketCommunicator, id)

    /**
     * @throws org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException if the signald
     * socket sends a bad or error response, or unable to serialize our request
     * @throws org.inthewaves.kotlinsignald.clientprotocol.SignaldException if an I/O error occurs
     * during socket communication
     * @throws GroupLinkNotActiveError
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     * @throws InvalidRequestError
     * @throws GroupVerificationError
     * @throws SQLError
     */
    public override suspend fun submitSuspend(
        socketCommunicator: SuspendSocketCommunicator,
        id: String
    ): JsonGroupJoinInfo = super.submitSuspend(socketCommunicator, id)
}
