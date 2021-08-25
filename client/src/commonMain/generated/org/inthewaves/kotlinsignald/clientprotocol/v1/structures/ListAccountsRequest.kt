package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.ListAccounts

/**
 * return all local accounts
 */
@Serializable
@SerialName("list_accounts")
public class ListAccountsRequest : SignaldRequestBodyV1<AccountList>() {
    internal override val responseWrapperSerializer: KSerializer<ListAccounts>
        get() = ListAccounts.serializer()

    internal override val responseDataSerializer: KSerializer<AccountList>
        get() = AccountList.serializer()

    internal override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        AccountList? = if (responseWrapper is ListAccounts && responseWrapper.data is
        AccountList
    ) {
        responseWrapper.data
    } else {
        null
    }
}
