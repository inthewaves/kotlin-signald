package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.Trust

/**
 * Trust another user's safety number using either the QR code data or the safety number text
 */
@Serializable
@SerialName("trust")
public data class TrustRequest(
    /**
     * The account to interact with
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * The user to query identity keys for
     */
    public val address: JsonAddress,
    /**
     * required if qr_code_data is absent
     * Example: "373453558586758076680580548714989751943247272727416091564451"
     */
    @SerialName("safety_number")
    public val safetyNumber: String? = null,
    /**
     * base64-encoded QR code data. required if safety_number is absent
     */
    @SerialName("qr_code_data")
    public val qrCodeData: String? = null,
    /**
     * One of TRUSTED_UNVERIFIED, TRUSTED_VERIFIED or UNTRUSTED. Default is TRUSTED_VERIFIED
     * Example: "TRUSTED_VERIFIED"
     */
    @SerialName("trust_level")
    public val trustLevel: String? = null
) : SignaldRequestBodyV1<EmptyResponse>() {
    internal override val responseWrapperSerializer: KSerializer<Trust>
        get() = Trust.serializer()

    internal override val responseDataSerializer: KSerializer<EmptyResponse>
        get() = EmptyResponse.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        EmptyResponse? = if (responseWrapper is Trust && responseWrapper.data is EmptyResponse) {
        responseWrapper.data
    } else {
        null
    }
}
