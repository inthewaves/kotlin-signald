package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.DeleteServer
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
@SerialName("delete_server")
public data class RemoveServerRequest(
    public val uuid: String? = null
) : SignaldRequestBodyV1<DeleteServer, EmptyResponse>() {
    protected override val responseWrapperSerializer: KSerializer<DeleteServer>
        get() = DeleteServer.serializer()

    protected override val responseDataSerializer: KSerializer<EmptyResponse>
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
