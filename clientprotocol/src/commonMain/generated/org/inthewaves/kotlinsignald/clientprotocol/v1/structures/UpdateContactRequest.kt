// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.SuspendSocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.UpdateContact

/**
 * update information about a local contact
 */
@Serializable
@SerialName("update_contact")
public data class UpdateContactRequest(
    /**
     * Example: "0cc10e61-d64c-4dbc-b51c-334f7dd45a4a"
     */
    public val account: String,
    public val address: JsonAddress,
    public val name: String? = null,
    public val color: String? = null,
    @SerialName("inbox_position")
    public val inboxPosition: Int? = null
) : SignaldRequestBodyV1<Profile>() {
    internal override val responseWrapperSerializer: KSerializer<UpdateContact>
        get() = UpdateContact.serializer()

    internal override val responseDataSerializer: KSerializer<Profile>
        get() = Profile.serializer()

    internal override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>): Profile? =
        if (responseWrapper is UpdateContact && responseWrapper.data is Profile) {
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
     * @throws AuthorizationFailedError
     * @throws SQLError
     * @throws NetworkError
     */
    public override fun submit(socketCommunicator: SocketCommunicator, id: String): Profile =
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
     * @throws AuthorizationFailedError
     * @throws SQLError
     * @throws NetworkError
     */
    public override suspend fun submitSuspend(
        socketCommunicator: SuspendSocketCommunicator,
        id: String
    ): Profile = super.submitSuspend(socketCommunicator, id)
}
