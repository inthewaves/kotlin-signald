// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.SuspendSocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.React

/**
 * react to a previous message
 */
@Serializable
@SerialName("react")
public data class ReactRequest(
    /**
     * Example: "+12024561414"
     */
    public val username: String,
    public val recipientAddress: JsonAddress? = null,
    /**
     * Example: "EdSqI90cS0UomDpgUXOlCoObWvQOXlH5G3Z2d3f4ayE="
     */
    public val recipientGroupId: String? = null,
    public val reaction: JsonReaction,
    public val timestamp: Long? = null,
    /**
     * Optionally set to a sub-set of group members. Ignored if recipientGroupId isn't specified
     */
    public val members: List<JsonAddress>? = null
) : SignaldRequestBodyV1<SendResponse>() {
    internal override val responseWrapperSerializer: KSerializer<React>
        get() = React.serializer()

    internal override val responseDataSerializer: KSerializer<SendResponse>
        get() = SendResponse.serializer()

    internal override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        SendResponse? = if (responseWrapper is React && responseWrapper.data is SendResponse) {
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
     * @throws InternalError
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
     * @throws NoSuchAccountError
     * @throws ServerNotFoundError
     * @throws InvalidProxyError
     * @throws InternalError
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
