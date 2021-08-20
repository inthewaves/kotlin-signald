package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
@Deprecated("Will be removed on Sat, 1 Jan 2022 09:01:01 GMT")
public data class GroupMember(
    /**
     * Example: "aeed01f0-a234-478e-8cf7-261c283151e7"
     */
    public val uuid: String? = null,
    /**
     * possible values are: UNKNOWN, DEFAULT, ADMINISTRATOR and UNRECOGNIZED
     * Example: "DEFAULT"
     */
    public val role: String? = null,
    @SerialName("joined_revision")
    public val joinedRevision: Int? = null
)
