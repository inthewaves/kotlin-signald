package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.serializers.UUIDSerializer
import java.util.UUID

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
public data class GroupMember(
    /**
     * Example: "aeed01f0-a234-478e-8cf7-261c283151e7"
     */
    @Serializable(UUIDSerializer::class)
    public val uuid: UUID? = null,
    /**
     * possible values are: UNKNOWN, DEFAULT, ADMINISTRATOR and UNRECOGNIZED
     * Example: "DEFAULT"
     */
    public val role: String? = null,
    @SerialName("joined_revision")
    public val joinedRevision: Int? = null
)
