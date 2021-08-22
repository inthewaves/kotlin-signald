package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.DeleteAccount
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper

/**
 * delete all account data signald has on disk, and optionally delete the account from the server as
 * well. Note that this is not "unlink" and will delete the entire account, even from a linked device.
 */
@Serializable
@SerialName("delete_account")
public data class DeleteAccountRequest(
    /**
     * The account to delete
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * delete account information from the server as well (default false)
     */
    public val server: Boolean? = null
) : SignaldRequestBodyV1<DeleteAccount, EmptyResponse>() {
    protected override val responseWrapperSerializer: KSerializer<DeleteAccount>
        get() = DeleteAccount.serializer()

    protected override val responseDataSerializer: KSerializer<EmptyResponse>
        get() = EmptyResponse.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        EmptyResponse? = if (responseWrapper is DeleteAccount && responseWrapper.data is
        EmptyResponse
    ) {
        responseWrapper.data
    } else {
        null
    }
}
