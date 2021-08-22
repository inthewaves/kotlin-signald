package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

@Serializable
public data class JsonGroupJoinInfo(
    /**
     * Example: "EdSqI90cS0UomDpgUXOlCoObWvQOXlH5G3Z2d3f4ayE="
     */
    public val groupID: String? = null,
    /**
     * Example: "Parkdale Run Club"
     */
    public val title: String? = null,
    /**
     * Example: 3
     */
    public val memberCount: Int? = null,
    public val addFromInviteLink: Int? = null,
    /**
     * Example: 5
     */
    public val revision: Int? = null,
    public val pendingAdminApproval: Boolean? = null
) : SignaldResponseBodyV1()
