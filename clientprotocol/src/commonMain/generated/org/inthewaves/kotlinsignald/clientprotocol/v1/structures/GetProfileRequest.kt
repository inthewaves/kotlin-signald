// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.SuspendSocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.GetProfile
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper

/**
 * Get all information available about a user
 */
@Serializable
@SerialName("get_profile")
public data class GetProfileRequest(
    /**
     * the signald account to use
     *
     * Example: "0cc10e61-d64c-4dbc-b51c-334f7dd45a4a"
     */
    public val account: String,
    /**
     * if true, return results from local store immediately, refreshing from server in the
     * background if needed. if false (default), block until profile can be retrieved from server
     */
    public val async: Boolean? = null,
    /**
     * the address to look up
     */
    public val address: JsonAddress
) : SignaldRequestBodyV1<Profile>() {
    internal override val responseWrapperSerializer: KSerializer<GetProfile>
        get() = GetProfile.serializer()

    internal override val responseDataSerializer: KSerializer<Profile>
        get() = Profile.serializer()

    internal override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>): Profile? =
        if (responseWrapper is GetProfile && responseWrapper.data is Profile) {
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
     * @throws ProfileUnavailableError
     * @throws UnregisteredUserError
     * @throws AuthorizationFailedError
     * @throws SQLError
     * @throws InvalidRequestError
     */
    public override fun submit(socketCommunicator: SocketCommunicator, id: String): Profile =
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
     * @throws ProfileUnavailableError
     * @throws UnregisteredUserError
     * @throws AuthorizationFailedError
     * @throws SQLError
     * @throws InvalidRequestError
     */
    public override suspend fun submitSuspend(
        socketCommunicator: SuspendSocketCommunicator,
        id: String
    ): Profile = super.submitSuspend(socketCommunicator, id)
}
