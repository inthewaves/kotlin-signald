package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v0.structures.JsonAttachment
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.Send

@Serializable
@SerialName("send")
public data class SendRequest(
    /**
     * Example: "+12024561414"
     */
    public val username: String,
    public val recipientAddress: JsonAddress? = null,
    /**
     * Example: "EdSqI90cS0UomDpgUXOlCoObWvQOXlH5G3Z2d3f4ayE="
     */
    public val recipientGroupId: String? = null,
    /**
     * Example: "hello"
     */
    public val messageBody: String? = null,
    public val attachments: List<JsonAttachment> = emptyList(),
    public val quote: JsonQuote? = null,
    public val timestamp: Long? = null,
    public val mentions: List<JsonMention> = emptyList()
) : SignaldRequestBodyV1<SendResponse>() {
    internal override val responseWrapperSerializer: KSerializer<Send>
        get() = Send.serializer()

    internal override val responseDataSerializer: KSerializer<SendResponse>
        get() = SendResponse.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        SendResponse? = if (responseWrapper is Send && responseWrapper.data is SendResponse) {
        responseWrapper.data
    } else {
        null
    }
}
