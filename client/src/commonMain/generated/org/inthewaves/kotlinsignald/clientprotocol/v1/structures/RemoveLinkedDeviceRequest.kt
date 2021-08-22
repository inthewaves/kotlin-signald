package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.RemoveLinkedDevice

/**
 * Remove a linked device from the Signal account. Only allowed when the local device id is 1
 */
@Serializable
@SerialName("remove_linked_device")
public data class RemoveLinkedDeviceRequest(
    /**
     * The account to interact with
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * the ID of the device to unlink
     * Example: 3
     */
    public val deviceId: Long
) : SignaldRequestBodyV1<RemoveLinkedDevice, EmptyResponse>() {
    protected override val responseWrapperSerializer: KSerializer<RemoveLinkedDevice>
        get() = RemoveLinkedDevice.serializer()

    protected override val responseDataSerializer: KSerializer<EmptyResponse>
        get() = EmptyResponse.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        EmptyResponse? = if (responseWrapper is RemoveLinkedDevice && responseWrapper.data is
        EmptyResponse
    ) {
        responseWrapper.data
    } else {
        null
    }
}
