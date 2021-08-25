package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.Verify

/**
 * verify an account's phone number with a code after registering, completing the account creation
 * process
 */
@Serializable
@SerialName("verify")
public data class VerifyRequest(
    /**
     * the e164 phone number being verified
     *
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * the verification code, dash (-) optional
     *
     * Example: "555555"
     */
    public val code: String
) : SignaldRequestBodyV1<Account>() {
    internal override val responseWrapperSerializer: KSerializer<Verify>
        get() = Verify.serializer()

    internal override val responseDataSerializer: KSerializer<Account>
        get() = Account.serializer()

    internal override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>): Account? =
        if (responseWrapper is Verify && responseWrapper.data is Account) {
            responseWrapper.data
        } else {
            null
        }
}
