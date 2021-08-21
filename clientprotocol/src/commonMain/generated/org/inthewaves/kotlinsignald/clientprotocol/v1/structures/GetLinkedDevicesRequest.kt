package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.GetLinkedDevices
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper

/**
 * list all linked devices on a Signal account
 */
@Serializable
@SerialName("get_linked_devices")
public data class GetLinkedDevicesRequest(
    /**
     * The account to interact with
     * Example: "+12024561414"
     */
    public val account: String
) : SignaldRequestBodyV1<GetLinkedDevices, LinkedDevices>() {
    protected override val responseWrapperSerializer: KSerializer<GetLinkedDevices>
        get() = GetLinkedDevices.serializer()

    protected override val responseDataSerializer: KSerializer<LinkedDevices>
        get() = LinkedDevices.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        LinkedDevices? = if (responseWrapper is GetLinkedDevices && responseWrapper.data is
        LinkedDevices
    ) {
        responseWrapper.data
    } else {
        null
    }
}
