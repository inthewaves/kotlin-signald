package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.RemoteDelete

/**
 * delete a message previously sent
 */
@Serializable
@SerialName("remote_delete")
public data class RemoteDeleteRequest(
    /**
     * the account to use
     *
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * the address to send the delete message to. should match address the message to be deleted was
     * sent to. required if group is not set.
     */
    public val address: JsonAddress? = null,
    /**
     * the group to send the delete message to. should match group the message to be deleted was
     * sent to. required if address is not set.
     *
     * Example: "EdSqI90cS0UomDpgUXOlCoObWvQOXlH5G3Z2d3f4ayE="
     */
    public val group: String? = null,
    public val timestamp: Long
) : SignaldRequestBodyV1<SendResponse>() {
    internal override val responseWrapperSerializer: KSerializer<RemoteDelete>
        get() = RemoteDelete.serializer()

    internal override val responseDataSerializer: KSerializer<SendResponse>
        get() = SendResponse.serializer()

    internal override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        SendResponse? = if (responseWrapper is RemoteDelete && responseWrapper.data is
        SendResponse
    ) {
        responseWrapper.data
    } else {
        null
    }
}
