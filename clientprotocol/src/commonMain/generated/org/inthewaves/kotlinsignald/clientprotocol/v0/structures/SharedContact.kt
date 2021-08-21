package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.Serializable

@Serializable
public data class SharedContact(
    public val name: Name? = null,
    public val avatar: Optional? = null,
    public val phone: Optional? = null,
    public val email: Optional? = null,
    public val address: Optional? = null,
    public val organization: Optional? = null
)
