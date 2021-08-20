package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.React

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 *
 * react to a previous message
 */
@Serializable
@SerialName("react")
public data class ReactRequest(
    /**
     * Example: "+12024561414"
     */
    public val username: String,
    public val recipientAddress: JsonAddress? = null,
    /**
     * Example: "EdSqI90cS0UomDpgUXOlCoObWvQOXlH5G3Z2d3f4ayE="
     */
    public val recipientGroupId: String? = null,
    public val reaction: JsonReaction,
    public val timestamp: Long? = null
) : SignaldRequestBodyV1<React, SendResponse>() {
    protected override val responseWrapperSerializer: KSerializer<React>
        get() = React.serializer()

    protected override val responseDataSerializer: KSerializer<SendResponse>
        get() = SendResponse.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        SendResponse? = if (responseWrapper is React && responseWrapper.data is SendResponse) {
        responseWrapper.data
    } else {
        null
    }
}
