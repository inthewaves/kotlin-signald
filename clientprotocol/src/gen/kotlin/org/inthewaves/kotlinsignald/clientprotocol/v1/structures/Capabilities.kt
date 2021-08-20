package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
public data class Capabilities(
    public val gv2: Boolean? = null,
    public val storage: Boolean? = null,
    @SerialName("gv1-migration")
    public val gv1Migration: Boolean? = null
)
