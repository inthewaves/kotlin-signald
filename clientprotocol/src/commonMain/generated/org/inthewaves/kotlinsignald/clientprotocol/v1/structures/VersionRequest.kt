package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.Version

@Serializable
@SerialName("version")
public class VersionRequest : SignaldRequestBodyV1<Version, JsonVersionMessage>() {
    protected override val responseWrapperSerializer: KSerializer<Version>
        get() = Version.serializer()

    protected override val responseDataSerializer: KSerializer<JsonVersionMessage>
        get() = JsonVersionMessage.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        JsonVersionMessage? = if (responseWrapper is Version && responseWrapper.data is
        JsonVersionMessage
    ) {
        responseWrapper.data
    } else {
        null
    }
}
