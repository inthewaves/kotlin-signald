// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.SuspendSocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.ResetSession

/**
 * reset a session with a particular user
 */
@Serializable
@SerialName("reset_session")
public data class ResetSessionRequest(
    /**
     * The account to use
     *
     * Example: "0cc10e61-d64c-4dbc-b51c-334f7dd45a4a"
     */
    public val account: String,
    /**
     * the user to reset session with
     */
    public val address: JsonAddress,
    public val timestamp: Long? = null
) : SignaldRequestBodyV1<SendResponse>() {
    internal override val responseWrapperSerializer: KSerializer<ResetSession>
        get() = ResetSession.serializer()

    internal override val responseDataSerializer: KSerializer<SendResponse>
        get() = SendResponse.serializer()

    internal override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        SendResponse? = if (responseWrapper is ResetSession && responseWrapper.data is
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
     * @throws ServerNotFoundError
     * @throws InvalidProxyError
     * @throws NoSuchAccountError
     * @throws InvalidRequestError
     * @throws UnknownGroupError
     * @throws RateLimitError
     * @throws InvalidRecipientError
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
     * @throws ServerNotFoundError
     * @throws InvalidProxyError
     * @throws NoSuchAccountError
     * @throws InvalidRequestError
     * @throws UnknownGroupError
     * @throws RateLimitError
     * @throws InvalidRecipientError
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
