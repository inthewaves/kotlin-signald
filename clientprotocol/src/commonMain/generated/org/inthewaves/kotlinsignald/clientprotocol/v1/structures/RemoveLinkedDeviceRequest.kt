// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.SuspendSocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.RemoveLinkedDevice

/**
 * Remove a linked device from the Signal account. Only allowed when the local device id is 1
 */
@Serializable
@SerialName("remove_linked_device")
public data class RemoveLinkedDeviceRequest(
    /**
     * The account to interact with
     *
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * the ID of the device to unlink
     *
     * Example: 3
     */
    public val deviceId: Long
) : SignaldRequestBodyV1<EmptyResponse>() {
    internal override val responseWrapperSerializer: KSerializer<RemoveLinkedDevice>
        get() = RemoveLinkedDevice.serializer()

    internal override val responseDataSerializer: KSerializer<EmptyResponse>
        get() = EmptyResponse.serializer()

    internal override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        EmptyResponse? = if (responseWrapper is RemoveLinkedDevice && responseWrapper.data is
        EmptyResponse
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
     * @throws InvalidRequestError
     * @throws AuthorizationFailedError
     * @throws SQLError
     */
    public override fun submit(socketCommunicator: SocketCommunicator, id: String): EmptyResponse =
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
     * @throws InvalidRequestError
     * @throws AuthorizationFailedError
     * @throws SQLError
     */
    public override suspend fun submitSuspend(
        socketCommunicator: SuspendSocketCommunicator,
        id: String
    ): EmptyResponse = super.submitSuspend(socketCommunicator, id)
}
