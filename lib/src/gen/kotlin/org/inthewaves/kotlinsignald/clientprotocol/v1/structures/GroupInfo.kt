package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 *
 * A generic type that is used when the group version is not known
 */
@Serializable
public data class GroupInfo(
    public val v1: JsonGroupInfo? = null,
    public val v2: JsonGroupV2Info? = null
) : SignaldResponseBodyV1()
