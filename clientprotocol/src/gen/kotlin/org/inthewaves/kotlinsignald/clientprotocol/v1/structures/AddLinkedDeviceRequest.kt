package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.AddDevice
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 *
 * Link a new device to a local Signal account
 */
@Serializable
@SerialName("add_device")
public data class AddLinkedDeviceRequest(
    /**
     * The account to interact with
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * the tsdevice:/ uri provided (typically in qr code form) by the new device
     * Example:
     * "tsdevice:/?uuid=jAaZ5lxLfh7zVw5WELd6-Q&pub_key=BfFbjSwmAgpVJBXUdfmSgf61eX3a%2Bq9AoxAVpl1HUap9"
     */
    public val uri: String
) : SignaldRequestBodyV1<AddDevice, EmptyResponse>() {
    protected override val responseWrapperSerializer: KSerializer<AddDevice>
        get() = AddDevice.serializer()

    protected override val responseDataSerializer: KSerializer<EmptyResponse>
        get() = EmptyResponse.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        EmptyResponse? = if (responseWrapper is AddDevice && responseWrapper.data is
        EmptyResponse
    ) {
        responseWrapper.data
    } else {
        null
    }
}
