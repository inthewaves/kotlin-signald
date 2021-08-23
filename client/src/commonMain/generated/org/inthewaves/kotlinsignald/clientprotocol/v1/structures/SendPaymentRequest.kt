package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.SendPayment

/**
 * send a mobilecoin payment
 */
@Serializable
@SerialName("send_payment")
public data class SendPaymentRequest(
    /**
     * the account to use
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * the address to send the payment message to
     */
    public val address: JsonAddress,
    public val payment: Payment,
    public val `when`: Long? = null
) : SignaldRequestBodyV1<SendResponse>() {
    internal override val responseWrapperSerializer: KSerializer<SendPayment>
        get() = SendPayment.serializer()

    internal override val responseDataSerializer: KSerializer<SendResponse>
        get() = SendResponse.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        SendResponse? = if (responseWrapper is SendPayment && responseWrapper.data is
        SendResponse
    ) {
        responseWrapper.data
    } else {
        null
    }
}
