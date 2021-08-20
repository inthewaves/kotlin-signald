package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.Verify

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 *
 * verify an account's phone number with a code after registering, completing the account creation
 * process
 */
@Serializable
@SerialName("verify")
public data class VerifyRequest(
    /**
     * the e164 phone number being verified
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * the verification code, dash (-) optional
     * Example: "555555"
     */
    public val code: String
) : SignaldRequestBodyV1<Verify, Account>() {
    protected override val responseWrapperSerializer: KSerializer<Verify>
        get() = Verify.serializer()

    protected override val responseDataSerializer: KSerializer<Account>
        get() = Account.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>): Account? =
        if (responseWrapper is Verify && responseWrapper.data is Account) {
            responseWrapper.data
        } else {
            null
        }
}
