package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.Register

/**
 * begin the account registration process by requesting a phone number verification code. when the
 * code is received, submit it with a verify request
 */
@Serializable
@SerialName("register")
public data class RegisterRequest(
    /**
     * the e164 phone number to register with
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * set to true to request a voice call instead of an SMS for verification
     */
    public val voice: Boolean? = null,
    /**
     * See https://signald.org/articles/captcha/
     */
    public val captcha: String? = null,
    /**
     * The identifier of the server to use. Leave blank for default (usually Signal production
     * servers but configurable at build time)
     */
    public val server: String? = null
) : SignaldRequestBodyV1<Register, Account>() {
    protected override val responseWrapperSerializer: KSerializer<Register>
        get() = Register.serializer()

    protected override val responseDataSerializer: KSerializer<Account>
        get() = Account.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>): Account? =
        if (responseWrapper is Register && responseWrapper.data is Account) {
            responseWrapper.data
        } else {
            null
        }
}