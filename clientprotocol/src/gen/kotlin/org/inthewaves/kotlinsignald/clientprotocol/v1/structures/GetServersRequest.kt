package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.GetServers
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
@SerialName("get_servers")
public class GetServersRequest : SignaldRequestBodyV1<GetServers, ServerList>() {
    protected override val responseWrapperSerializer: KSerializer<GetServers>
        get() = GetServers.serializer()

    protected override val responseDataSerializer: KSerializer<ServerList>
        get() = ServerList.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>): ServerList? =
        if (responseWrapper is GetServers && responseWrapper.data is ServerList) {
            responseWrapper.data
        } else {
            null
        }
}
