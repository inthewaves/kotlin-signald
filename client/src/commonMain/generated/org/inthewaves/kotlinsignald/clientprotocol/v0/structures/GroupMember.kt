package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Deprecated("Will be removed after Sat, 1 Jan 2022 09:01:01 GMT")
public data class GroupMember(
    /**
     * Example: "aeed01f0-a234-478e-8cf7-261c283151e7"
     */
    public val uuid: String? = null,
    /**
     * possible values are: UNKNOWN, DEFAULT, ADMINISTRATOR and UNRECOGNIZED
     *
     * Example: "DEFAULT"
     */
    public val role: String? = null,
    @SerialName("joined_revision")
    public val joinedRevision: Int? = null
)
