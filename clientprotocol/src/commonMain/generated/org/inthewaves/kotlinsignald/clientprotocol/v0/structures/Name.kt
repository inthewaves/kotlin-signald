package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.Serializable

@Serializable
public data class Name(
    public val display: Optional? = null,
    public val given: Optional? = null,
    public val family: Optional? = null,
    public val prefix: Optional? = null,
    public val suffix: Optional? = null,
    public val middle: Optional? = null
)
