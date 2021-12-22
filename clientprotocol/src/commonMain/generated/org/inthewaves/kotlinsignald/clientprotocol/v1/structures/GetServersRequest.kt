// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.SuspendSocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.GetServers
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper

@Serializable
@SerialName("get_servers")
public class GetServersRequest : SignaldRequestBodyV1<ServerList>() {
    internal override val responseWrapperSerializer: KSerializer<GetServers>
        get() = GetServers.serializer()

    internal override val responseDataSerializer: KSerializer<ServerList>
        get() = ServerList.serializer()

    internal override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        ServerList? = if (responseWrapper is GetServers && responseWrapper.data is ServerList) {

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
     */
    public override fun submit(socketCommunicator: SocketCommunicator, id: String): ServerList =
        super.submit(socketCommunicator, id)

    /**
     * @throws org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException if the signald
     * socket sends a bad or error response, or unable to serialize our request
     * @throws org.inthewaves.kotlinsignald.clientprotocol.SignaldException if an I/O error occurs
     * during socket communication
     * @throws InternalError
     */
    public override suspend fun submitSuspend(
        socketCommunicator: SuspendSocketCommunicator,
        id: String
    ): ServerList = super.submitSuspend(socketCommunicator, id)
}
