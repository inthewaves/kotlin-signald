package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.SetDeviceName

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 *
 * set this device's name. This will show up on the mobile device on the same account under
 */
@Serializable
@SerialName("set_device_name")
public data class SetDeviceNameRequest(
    /**
     * The account to set the device name of
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * The device name
     */
    @SerialName("device_name")
    public val deviceName: String? = null
) : SignaldRequestBodyV1<SetDeviceName, EmptyResponse>() {
    protected override val responseWrapperSerializer: KSerializer<SetDeviceName>
        get() = SetDeviceName.serializer()

    protected override val responseDataSerializer: KSerializer<EmptyResponse>
        get() = EmptyResponse.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        EmptyResponse? = if (responseWrapper is SetDeviceName && responseWrapper.data is
        EmptyResponse
    ) {
        responseWrapper.data
    } else {
        null
    }
}
