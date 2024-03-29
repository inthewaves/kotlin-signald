// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.SuspendSocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.RemoteDelete

/**
 * delete a message previously sent
 */
@Serializable
@SerialName("remote_delete")
public data class RemoteDeleteRequest(
    /**
     * the account to use
     *
     * Example: "0cc10e61-d64c-4dbc-b51c-334f7dd45a4a"
     */
    public val account: String,
    /**
     * the address to send the delete message to. should match address the message to be deleted was
     * sent to. required if group is not set.
     */
    public val address: JsonAddress? = null,
    /**
     * the group to send the delete message to. should match group the message to be deleted was
     * sent to. required if address is not set.
     *
     * Example: "EdSqI90cS0UomDpgUXOlCoObWvQOXlH5G3Z2d3f4ayE="
     */
    public val group: String? = null,
    public val timestamp: Long,
    /**
     * Optionally set to a sub-set of group members. Ignored if group isn't specified
     */
    public val members: List<JsonAddress>? = null
) : SignaldRequestBodyV1<SendResponse>() {
    internal override val responseWrapperSerializer: KSerializer<RemoteDelete>
        get() = RemoteDelete.serializer()

    internal override val responseDataSerializer: KSerializer<SendResponse>
        get() = SendResponse.serializer()

    internal override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        SendResponse? = if (responseWrapper is RemoteDelete && responseWrapper.data is
        SendResponse
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
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     * @throws InvalidRecipientError
     * @throws UnknownGroupError
     * @throws InvalidRequestError
     * @throws RateLimitError
     * @throws UnregisteredUserError
     * @throws AuthorizationFailedError
     * @throws SQLError
     * @throws ProofRequiredError
     * @throws SignalServerError
     */
    public override fun submit(socketCommunicator: SocketCommunicator, id: String): SendResponse =
        super.submit(socketCommunicator, id)

    /**
     * @throws org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException if the signald
     * socket sends a bad or error response, or unable to serialize our request
     * @throws org.inthewaves.kotlinsignald.clientprotocol.SignaldException if an I/O error occurs
     * during socket communication
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     * @throws InvalidRecipientError
     * @throws UnknownGroupError
     * @throws InvalidRequestError
     * @throws RateLimitError
     * @throws UnregisteredUserError
     * @throws AuthorizationFailedError
     * @throws SQLError
     * @throws ProofRequiredError
     * @throws SignalServerError
     */
    public override suspend fun submitSuspend(
        socketCommunicator: SuspendSocketCommunicator,
        id: String
    ): SendResponse = super.submitSuspend(socketCommunicator, id)
}
