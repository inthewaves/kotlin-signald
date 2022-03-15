// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.SuspendSocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper

/**
 * get all known identity keys
 */
@Serializable
@SerialName("get_all_identities")
public data class GetAllIdentities(
    /**
     * The account to interact with
     *
     * Example: "+12024561414"
     */
    public val account: String
) : SignaldRequestBodyV1<AllIdentityKeyList>() {
    internal override val responseWrapperSerializer:
        KSerializer<org.inthewaves.kotlinsignald.clientprotocol.v1.requests.GetAllIdentities>
            get() =
                org.inthewaves.kotlinsignald.clientprotocol.v1.requests.GetAllIdentities.serializer()

    internal override val responseDataSerializer: KSerializer<AllIdentityKeyList>
        get() = AllIdentityKeyList.serializer()

    internal override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        AllIdentityKeyList? = if (responseWrapper is
        org.inthewaves.kotlinsignald.clientprotocol.v1.requests.GetAllIdentities &&
        responseWrapper.data is AllIdentityKeyList
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
     * @throws InvalidProxyError
     * @throws NoSuchAccountError
     * @throws ServerNotFoundError
     * @throws InternalError
     * @throws AuthorizationFailedError
     * @throws SQLError
     */
    public override fun submit(socketCommunicator: SocketCommunicator, id: String):
        AllIdentityKeyList = super.submit(socketCommunicator, id)

    /**
     * @throws org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException if the signald
     * socket sends a bad or error response, or unable to serialize our request
     * @throws org.inthewaves.kotlinsignald.clientprotocol.SignaldException if an I/O error occurs
     * during socket communication
     * @throws InvalidProxyError
     * @throws NoSuchAccountError
     * @throws ServerNotFoundError
     * @throws InternalError
     * @throws AuthorizationFailedError
     * @throws SQLError
     */
    public override suspend fun submitSuspend(
        socketCommunicator: SuspendSocketCommunicator,
        id: String
    ): AllIdentityKeyList = super.submitSuspend(socketCommunicator, id)
}
