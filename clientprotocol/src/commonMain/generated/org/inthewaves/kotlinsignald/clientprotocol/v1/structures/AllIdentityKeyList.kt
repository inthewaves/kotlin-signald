package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class AllIdentityKeyList(
    @SerialName("identity_keys")
    public val identityKeys: List<IdentityKeyList> = emptyList()
) : SignaldResponseBodyV1()
