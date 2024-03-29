// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Information about a Signal group
 */
@Serializable
public data class JsonGroupV2Info(
    /**
     * Example: "EdSqI90cS0UomDpgUXOlCoObWvQOXlH5G3Z2d3f4ayE="
     */
    public val id: String? = null,
    /**
     * Example: 5
     */
    public val revision: Int? = null,
    /**
     * Example: "Parkdale Run Club"
     */
    public val title: String? = null,
    public val description: String? = null,
    /**
     * path to the group's avatar on local disk, if available
     *
     * Example: "/var/lib/signald/avatars/group-EdSqI90cS0UomDpgUXOlCoObWvQOXlH5G3Z2d3f4ayE="
     */
    public val avatar: String? = null,
    /**
     * Example: 604800
     */
    public val timer: Int? = null,
    public val members: List<JsonAddress> = emptyList(),
    public val pendingMembers: List<JsonAddress> = emptyList(),
    public val requestingMembers: List<JsonAddress> = emptyList(),
    /**
     * the signal.group link, if applicable
     */
    public val inviteLink: String? = null,
    /**
     * current access control settings for this group
     */
    public val accessControl: GroupAccessControl? = null,
    /**
     * detailed member list
     */
    public val memberDetail: List<GroupMember> = emptyList(),
    /**
     * detailed pending member list
     */
    public val pendingMemberDetail: List<GroupMember> = emptyList(),
    /**
     * indicates if the group is an announcements group. Only admins are allowed to send messages to
     * announcements groups. Options are UNKNOWN, ENABLED or DISABLED
     */
    public val announcements: String? = null,
    /**
     * will be set to true for incoming messages to indicate the user has been removed from the
     * group
     */
    public val removed: Boolean? = null,
    @SerialName("banned_members")
    public val bannedMembers: List<BannedGroupMember> = emptyList(),
    /**
     * Represents a peer-to-peer group change done by a user. Will not be set if the group change
     * signature fails verification. This is usually only set inside of incoming messages.
     */
    @SerialName("group_change")
    public val groupChange: GroupChange? = null
) : SignaldResponseBodyV1()
