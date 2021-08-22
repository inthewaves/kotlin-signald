package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

/**
 * a list of identity keys associated with a particular address
 */
@Serializable
public data class IdentityKeyList(
    public val address: JsonAddress? = null,
    public val identities: List<IdentityKey> = emptyList()
) : SignaldResponseBodyV1()