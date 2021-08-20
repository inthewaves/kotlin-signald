package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 *
 * a list of identity keys associated with a particular address
 */
@Serializable
public data class IdentityKeyList(
    public val address: JsonAddress? = null,
    public val identities: List<IdentityKey> = emptyList()
) : SignaldResponseBodyV1()
