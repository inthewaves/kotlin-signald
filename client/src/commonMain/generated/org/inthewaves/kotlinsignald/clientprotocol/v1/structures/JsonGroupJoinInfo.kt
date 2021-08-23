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
     * Example: "A club for running in Parkdale"
     */
    public val description: String? = null,
    /**
     * Example: 3
     */
    public val memberCount: Int? = null,
    /**
     * The access level required in order to join the group from the invite link, as an
     * AccessControl.AccessRequired enum from the upstream Signal groups.proto file. This is
     * UNSATISFIABLE (4) when the group link is disabled; ADMINISTRATOR (3) when the group link is
     * enabled, but an administrator must approve new members; and ANY (1) when the group link is
     * enabled and no approval is required. See theGroupAccessControl structure and the upstream enum
     * ordinals.
     */
    public val addFromInviteLink: Int? = null,
    /**
     * The Group V2 revision. This is incremented by clients whenever they update group information,
     * and it is often used by clients to determine if the local group state is out-of-date with the
     * server's revision.
     * Example: 5
     */
    public val revision: Int? = null,
    /**
     * Whether the account is waiting for admin approval in order to be added to the group.
     */
    public val pendingAdminApproval: Boolean? = null
) : SignaldResponseBodyV1()
