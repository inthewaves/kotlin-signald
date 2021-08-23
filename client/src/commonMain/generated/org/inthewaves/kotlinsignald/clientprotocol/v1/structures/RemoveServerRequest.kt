package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.DeleteServer
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper

@Serializable
@SerialName("delete_server")
public data class RemoveServerRequest(
    public val uuid: String? = null
) : SignaldRequestBodyV1<EmptyResponse>() {
    internal override val responseWrapperSerializer: KSerializer<DeleteServer>
        get() = DeleteServer.serializer()

    internal override val responseDataSerializer: KSerializer<EmptyResponse>
        get() = EmptyResponse.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        EmptyResponse? = if (responseWrapper is DeleteServer && responseWrapper.data is
        EmptyResponse
    ) {
        responseWrapper.data
    } else {
        null
    }
}
