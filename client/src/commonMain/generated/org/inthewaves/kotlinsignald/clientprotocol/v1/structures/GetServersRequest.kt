package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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
}
