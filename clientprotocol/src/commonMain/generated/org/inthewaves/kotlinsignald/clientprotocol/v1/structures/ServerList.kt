package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

@Serializable
public data class ServerList(
    public val servers: List<Server> = emptyList()
) : SignaldResponseBodyV1()
