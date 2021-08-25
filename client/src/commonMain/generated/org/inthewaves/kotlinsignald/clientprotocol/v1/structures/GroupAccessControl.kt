package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

/**
 * group access control settings. Options for each controlled action are: UNKNOWN, ANY, MEMBER,
 * ADMINISTRATOR, UNSATISFIABLE and UNRECOGNIZED
 */
@Serializable
public data class GroupAccessControl(
    /**
     * UNSATISFIABLE when the group link is disabled, ADMINISTRATOR when the group link is enabled
     * but an administrator must approve new members, ANY when the group link is enabled and no
     * approval is required
     *
     * Example: "ANY"
     */
    public val link: String? = null,
    /**
     * who can edit group info
     */
    public val attributes: String? = null,
    /**
     * who can add members
     */
    public val members: String? = null
)
