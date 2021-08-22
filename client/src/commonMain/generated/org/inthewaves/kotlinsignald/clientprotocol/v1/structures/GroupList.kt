package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

@Serializable
public data class GroupList(
    public val groups: List<JsonGroupV2Info> = emptyList(),
    public val legacyGroups: List<JsonGroupInfo> = emptyList()
) : SignaldResponseBodyV1()
