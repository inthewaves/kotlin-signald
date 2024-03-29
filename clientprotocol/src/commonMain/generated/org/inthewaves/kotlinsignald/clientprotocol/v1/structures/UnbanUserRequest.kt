// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.SuspendSocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.UnbanUser

/**
 * Unbans users from a group.
 */
@Serializable
@SerialName("unban_user")
public data class UnbanUserRequest(
    /**
     * The account to interact with
     *
     * Example: "0cc10e61-d64c-4dbc-b51c-334f7dd45a4a"
     */
    public val account: String,
    /**
     * Example: "EdSqI90cS0UomDpgUXOlCoObWvQOXlH5G3Z2d3f4ayE="
     */
    @SerialName("group_id")
    public val groupId: String,
    /**
     * List of users to unban
     */
    public val users: List<JsonAddress>
) : SignaldRequestBodyV1<JsonGroupV2Info>() {
    internal override val responseWrapperSerializer: KSerializer<UnbanUser>
        get() = UnbanUser.serializer()

    internal override val responseDataSerializer: KSerializer<JsonGroupV2Info>
        get() = JsonGroupV2Info.serializer()

    internal override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        JsonGroupV2Info? = if (responseWrapper is UnbanUser && responseWrapper.data is
        JsonGroupV2Info
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
     * @throws NoSuchAccountError
     * @throws ServerNotFoundError
     * @throws InvalidProxyError
     * @throws UnknownGroupError
     * @throws GroupVerificationError
     * @throws InternalError
     * @throws InvalidRequestError
     * @throws AuthorizationFailedError Can be caused if signald is setup as a linked device that
     * has been removed by the primary device. If trying to update a group, this can also be caused if
     * group permissions don't allow the update  (e.g. current role insufficient or not a member).
     * @throws SQLError
     * @throws GroupPatchNotAcceptedError Caused when server rejects the group update.
     */
    public override fun submit(socketCommunicator: SocketCommunicator, id: String): JsonGroupV2Info =
        super.submit(socketCommunicator, id)

    /**
     * @throws org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException if the signald
     * socket sends a bad or error response, or unable to serialize our request
     * @throws org.inthewaves.kotlinsignald.clientprotocol.SignaldException if an I/O error occurs
     * during socket communication
     * @throws NoSuchAccountError
     * @throws ServerNotFoundError
     * @throws InvalidProxyError
     * @throws UnknownGroupError
     * @throws GroupVerificationError
     * @throws InternalError
     * @throws InvalidRequestError
     * @throws AuthorizationFailedError Can be caused if signald is setup as a linked device that
     * has been removed by the primary device. If trying to update a group, this can also be caused if
     * group permissions don't allow the update  (e.g. current role insufficient or not a member).
     * @throws SQLError
     * @throws GroupPatchNotAcceptedError Caused when server rejects the group update.
     */
    public override suspend fun submitSuspend(
        socketCommunicator: SuspendSocketCommunicator,
        id: String
    ): JsonGroupV2Info = super.submitSuspend(socketCommunicator, id)
}
