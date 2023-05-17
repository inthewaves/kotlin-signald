// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.SuspendSocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.SendPayment

/**
 * send a mobilecoin payment
 */
@Serializable
@SerialName("send_payment")
public data class SendPaymentRequest(
    /**
     * the account to use
     *
     * Example: "0cc10e61-d64c-4dbc-b51c-334f7dd45a4a"
     */
    public val account: String,
    /**
     * the address to send the payment message to
     */
    public val address: JsonAddress,
    public val payment: Payment,
    public val `when`: Long? = null
) : SignaldRequestBodyV1<SendResponse>() {
    internal override val responseWrapperSerializer: KSerializer<SendPayment>
        get() = SendPayment.serializer()

    internal override val responseDataSerializer: KSerializer<SendResponse>
        get() = SendResponse.serializer()

    internal override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        SendResponse? = if (responseWrapper is SendPayment && responseWrapper.data is
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
     * @throws InvalidBase64Error
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
     * @throws InvalidBase64Error
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
