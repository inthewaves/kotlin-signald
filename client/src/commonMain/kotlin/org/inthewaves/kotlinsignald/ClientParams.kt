package org.inthewaves.kotlinsignald

import org.inthewaves.kotlinsignald.Recipient.Group
import org.inthewaves.kotlinsignald.Recipient.Individual
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GroupAccessControl
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GroupMember
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.IncomingMessage
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonAddress
import kotlin.jvm.JvmStatic

/**
 * Specifies a recipient type for functions that can send to either type (e.g., `react`, `remoteDelete`, `send`,
 * `setExpiration`, `typing`). Recipients are either to a [Group] or an [Individual].
 */
public sealed class Recipient {
    /**
     * A group as the recipient, using the base64-encoded [groupID] as the identifier. [groupID]s can be retrieved
     * with `listGroups` or by reading the responses from `joinGroup` or `getGroupLinkInfo`.
     */
    public class Group(public val groupID: String) : Recipient()

    /**
     * A Signal user as the recipient.
     */
    public class Individual(public val address: JsonAddress) : Recipient()

    public companion object {
        @JvmStatic
        public fun forGroup(groupID: String): Group = Group(groupID)

        @JvmStatic
        public fun forIndividual(address: JsonAddress): Individual = Individual(address)

        @JvmStatic
        public fun forReply(incomingMessage: IncomingMessage): Recipient {
            val gv2Id = incomingMessage.data.dataMessage?.groupV2?.id
            return if (gv2Id != null) {
                Group(groupID = gv2Id)
            } else {
                Individual(address = incomingMessage.data.source!!)
            }
        }

        @JvmStatic
        public fun forNumber(number: String): Individual = Individual(JsonAddress(number = number))

        @JvmStatic
        public fun forUUID(uuid: String): Individual = Individual(JsonAddress(uuid = uuid))
    }
}

/**
 * Used with the `updateGroup` function. This is a class to enforce that only one of the group attributes are updated
 * at once.
 */
public sealed interface GroupUpdate {
    public class Title(public val newTitle: String) : GroupUpdate
    public class Description(public val newDescription: String) : GroupUpdate
    public class Avatar(public val newAvatarPath: String) : GroupUpdate

    /**
     * @property newTimerSeconds The new disappearing message timer in seconds. Set to 0 to disable
     */
    public class UpdateExpirationTimer(public val newTimerSeconds: Int) : GroupUpdate
    public class AddMembers(public val membersToAdd: Iterable<JsonAddress>) : GroupUpdate
    public class RemoveMembers(public val membersToRemove: Iterable<JsonAddress>) : GroupUpdate
    public class UpdateRole(public val memberWithUpdatedRole: GroupMember) : GroupUpdate
    public class UpdateAccessControl(update: AccessControlUpdate) : GroupUpdate {
        public val groupAccessControl: GroupAccessControl = update.groupAccessControlBody
    }

    /**
     * Whether to only allow admins to post messages.
     */
    public class SetAnnouncement(public val setAnnouncementOnly: Boolean) : GroupUpdate

    public object ResetLink : GroupUpdate
}

/**
 * Used with the [updateGroup] function.
 */
public enum class GroupLinkStatus {
    OFF, ON_NO_ADMIN_APPROVAL, ON_WITH_ADMIN_APPROVAL
}

/**
 * Used with the [trust] function.
 */
public sealed class Fingerprint {
    public class SafetyNumber(public val safetyNumber: String) : Fingerprint()

    /**
     * @param qrCodeData base64-encoded QR code data.
     */
    public class QrCodeData(public val qrCodeData: String) : Fingerprint()
}

/**
 * Used with the [updateGroup] function.
 *
 * Derived from
 * [https://github.com/signalapp/Signal-Android/blob/c615b14c512d3b0fffec3da93f8e2e3607ed6ab4/libsignal/service/src/main/proto/Groups.proto#L49]
 */
public enum class AccessRequired(public val value: Int) {
    UNRECOGNIZED(-1),
    UNKNOWN(0),
    ANY(1),
    MEMBER(2),
    ADMINISTRATOR(3),
    UNSATISFIABLE(4),
}

/**
 * Used with the [updateGroup] function inside of [GroupUpdate]. A class to enforce that only one of the access
 * controls are updated at once.
 */
public sealed class AccessControlUpdate {
    internal abstract val accessRequired: AccessRequired

    /**
     * The body of the [GroupAccessControl] to perform the change.
     */
    public val groupAccessControlBody: GroupAccessControl
        get() = when (this) {
            is GroupLink -> GroupAccessControl(link = accessRequired.name)
            is Attributes -> GroupAccessControl(attributes = accessRequired.name)
            is Members -> GroupAccessControl(attributes = accessRequired.name)
        }

    /**
     * Edit the group link status.
     */
    public class GroupLink(newGroupLinkStatus: GroupLinkStatus) : AccessControlUpdate() {
        override val accessRequired: AccessRequired = when (newGroupLinkStatus) {
            GroupLinkStatus.OFF -> AccessRequired.UNSATISFIABLE
            GroupLinkStatus.ON_NO_ADMIN_APPROVAL -> AccessRequired.ANY
            GroupLinkStatus.ON_WITH_ADMIN_APPROVAL -> AccessRequired.ADMINISTRATOR
        }
    }

    /**
     * Edit the access required to edit group information.
     */
    public class Attributes(public override val accessRequired: AccessRequired) : AccessControlUpdate()

    /**
     * Edit the access required to add new members.
     */
    public class Members(public override val accessRequired: AccessRequired) : AccessControlUpdate()
}

/**
 * Used with the [trust] function.
 */
public enum class TrustLevel {
    TRUSTED_UNVERIFIED, TRUSTED_VERIFIED, UNTRUSTED
}
