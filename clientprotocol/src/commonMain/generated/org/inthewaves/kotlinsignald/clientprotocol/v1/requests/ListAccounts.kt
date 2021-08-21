package org.inthewaves.kotlinsignald.clientprotocol.v1.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.AccountList

/**
 * This class only represents the response from signald for the request. Make a request by creating
 * an instance of [org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ListAccountsRequest] and
 * then calling its `submit` function.
 */
@Serializable
@SerialName("list_accounts")
public data class ListAccounts private constructor(
    public override val `data`: AccountList? = null
) : JsonMessageWrapper<AccountList>()
