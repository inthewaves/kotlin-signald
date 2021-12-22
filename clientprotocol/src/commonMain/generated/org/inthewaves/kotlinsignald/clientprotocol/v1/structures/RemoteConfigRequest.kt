// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.SuspendSocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.GetRemoteConfig
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper

/**
 * Retrieves the remote config (feature flags) from the server.
 */
@Serializable
@SerialName("get_remote_config")
public data class RemoteConfigRequest(
    /**
     * The account to use to retrieve the remote config
     *
     * Example: "+12024561414"
     */
    public val account: String
) : SignaldRequestBodyV1<RemoteConfigList>() {
    internal override val responseWrapperSerializer: KSerializer<GetRemoteConfig>
        get() = GetRemoteConfig.serializer()

    internal override val responseDataSerializer: KSerializer<RemoteConfigList>
        get() = RemoteConfigList.serializer()

    internal override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        RemoteConfigList? = if (responseWrapper is GetRemoteConfig && responseWrapper.data is
        RemoteConfigList
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
     */
    public override fun submit(socketCommunicator: SocketCommunicator, id: String): RemoteConfigList =
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
     */
    public override suspend fun submitSuspend(
        socketCommunicator: SuspendSocketCommunicator,
        id: String
    ): RemoteConfigList = super.submitSuspend(socketCommunicator, id)
}
