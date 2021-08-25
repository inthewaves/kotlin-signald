package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

@Serializable
public data class ProfileList(
    public val profiles: List<Profile> = emptyList()
) : SignaldResponseBodyV1()
