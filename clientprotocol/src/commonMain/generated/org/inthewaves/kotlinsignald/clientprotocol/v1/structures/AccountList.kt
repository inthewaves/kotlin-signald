// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

@Serializable
public data class AccountList(
    public val accounts: List<Account> = emptyList()
) : SignaldResponseBodyV1()
