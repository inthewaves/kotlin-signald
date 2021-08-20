package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.Serializable

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
public data class Name(
    public val display: Optional? = null,
    public val given: Optional? = null,
    public val family: Optional? = null,
    public val prefix: Optional? = null,
    public val suffix: Optional? = null,
    public val middle: Optional? = null
)
