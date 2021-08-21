package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.AddServer
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper

/**
 * add a new server to connect to. Returns the new server's UUID.
 */
@Serializable
@SerialName("add_server")
public data class AddServerRequest(
    public val server: Server
) : SignaldRequestBodyV1<AddServer, String>() {
    protected override val responseWrapperSerializer: KSerializer<AddServer>
        get() = AddServer.serializer()

    protected override val responseDataSerializer: KSerializer<String>
        get() = String.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>): String? = if
    (responseWrapper is AddServer && responseWrapper.data is String) {
        responseWrapper.data
    } else {
        null
    }
}
