package org.inthewaves.kotlinsignald

import org.inthewaves.kotlinsignald.Recipient.Group
import org.inthewaves.kotlinsignald.Recipient.Individual
import org.inthewaves.kotlinsignald.Recipient.Self
import org.inthewaves.kotlinsignald.SyncRequest.MessageRequestResponse.Action
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GroupAccessControl
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GroupMember
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.IncomingMessage
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonAddress
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonMessageRequestResponseMessage
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonViewOnceOpenMessage
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.SendSyncMessageRequest
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * Used with the [org.inthewaves.kotlinsignald.Signal.submitChallenge] function. Specifies a submission to a rate limit
 * challenge issued by the server.
 */
public sealed class RateLimitChallenge(public val challengeToken: String) {
    public class PushChallenge(challengeToken: String) : RateLimitChallenge(challengeToken)

    /**
     * Responding to a server's rate limit challenge that includes CAPTCHA as one of the options for the challenge.
     *
     * The [captchaToken] should be obtained from Signal-Android's `RECAPTCHA_PROOF_URL` BuildConfig field in
     * app/build.gradle (usually https://signalcaptchas.org/challenge/generate.html) and then completing a reCAPTCHA
     * there.
     */
    public class Recaptcha(challengeToken: String, public val captchaToken: String) : RateLimitChallenge(challengeToken)
}

/**
 * Specifies a recipient type for functions that can send to either type (e.g.,
 * [org.inthewaves.kotlinsignald.Signal.react], [org.inthewaves.kotlinsignald.Signal.remoteDelete],
 * [org.inthewaves.kotlinsignald.Signal.send], [org.inthewaves.kotlinsignald.Signal.setExpiration],
 * [org.inthewaves.kotlinsignald.Signal.typing]). Recipients are either to a [Group] or an [Individual] (or [Self] for
 * Note to Self).
 */
public sealed class Recipient {
    /**
     * A group as the recipient, using the base64-encoded [groupID] as the identifier. [groupID]s can be retrieved
     * with `listGroups` or by reading the responses from `joinGroup` or `getGroupLinkInfo`.
     *
     * If a [memberSubset] is non-empty, messages will only be sent to that subset. This is useful for handling message
     * send failures for only certain members of the group (e.g. safety number changes).
     */
    public class Group(
        public val groupID: String,
        public val memberSubset: Collection<JsonAddress> = emptyList()
    ) : Recipient() {
        /**
         * Creates a copy of this [Group] with the same groupID but using the given [memberSubset]. This is useful for
         * handling message send failures for only certain members of the group (e.g. safety number changes).
         */
        public fun withMemberSubset(memberSubset: Collection<JsonAddress>): Group = Group(groupID, memberSubset)
    }

    /**
     * A Signal user as the recipient.
     */
    public class Individual(public val address: JsonAddress) : Recipient()

    /**
     * For sending to own user (i.e., Note to Self).
     */
    public object Self : Recipient()

    public companion object {
        /**
         * Constructs a recipient type for sending a message/reaction/etc. to a group. If [memberSubset] is non-empty,
         * the message will only be sent to members in the [memberSubset]. This is useful for handling message
         * send failures for only certain members of the group (e.g. safety number changes).
         */
        @JvmStatic
        @JvmOverloads
        public fun forGroup(groupID: String, memberSubset: Collection<JsonAddress> = emptyList()): Group = Group(
            groupID,
            memberSubset
        )

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

        @JvmStatic
        public fun forSelf(): Self = Self
    }
}

/**
 * Used with the [org.inthewaves.kotlinsignald.Signal.updateGroup] function. This is a class to enforce that only one of
 * the group attributes are updated at once.
 */
public sealed interface GroupUpdate {
    public class Title(public val newTitle: String) : GroupUpdate
    public class Description(public val newDescription: String) : GroupUpdate
    public class Avatar(public val newAvatarPath: String) : GroupUpdate

    /**
     * @property newTimerSeconds The new disappearing message timer in seconds. Set to 0 to disable
     */
    public class UpdateExpirationTimer(public val newTimerSeconds: Int) : GroupUpdate
    public class AddMembers(public val membersToAdd: Collection<JsonAddress>) : GroupUpdate
    public class RemoveMembers(public val membersToRemove: Collection<JsonAddress>) : GroupUpdate
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
 * Used with the [org.inthewaves.kotlinsignald.Signal.leaveGroup] function.
 */
public enum class LeaveGroupType {
    /** Leaves the group without deleting the group chat from other devices. */
    LEAVE_ONLY,
    /** Requests deletion of the group chat from other devices, and then leaves the group */
    DELETE_FROM_OTHER_DEVICES,
    /**
     * Requests blocking and deletion of the group chat from other devices, and then leaves the group. Note that
     * signald itself doesn't support blocking right now; however, this will request that other devices add it to
     * block list
     */
    BLOCK_AND_DELETE_FROM_OTHER_DEVICES,
}

/**
 * Used with the [org.inthewaves.kotlinsignald.Signal.trust] function.
 */
public sealed class Fingerprint {
    public class SafetyNumber(public val safetyNumber: String) : Fingerprint()

    /**
     * @param qrCodeData base64-encoded QR code data.
     */
    public class QrCodeData(public val qrCodeData: String) : Fingerprint()
}

/**
 * Used with the [org.inthewaves.kotlinsignald.Signal.updateGroup] function.
 */
public enum class GroupLinkStatus {
    OFF, ON_NO_ADMIN_APPROVAL, ON_WITH_ADMIN_APPROVAL
}

/**
 * Used with the [org.inthewaves.kotlinsignald.Signal.updateGroup] function.
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
 * Used with the [org.inthewaves.kotlinsignald.Signal.sendSyncRequest] function for [SendSyncMessageRequest] to send
 * sync requests to other devices.
 */
public sealed class SyncRequest {
    /**
     * For use in the [org.inthewaves.kotlinsignald.Signal.sendSyncRequest] to respond to incoming message requests.
     * Users and group conversations can be requested to be deleted from other devices, and it's also possible to
     * request other devices block messages (note that signald itself doesn't handle blocking at the moment).
     *
     * Corresponds to constructing an instance of [JsonMessageRequestResponseMessage].
     *
     * Warning: Using the [Action.BLOCK] and [Action.BLOCK_AND_DELETE] options relies on other devices to do the
     * blocking, and it does not make you leave the group!
     */
    public class MessageRequestResponse(public val recipient: Recipient, public val action: Action) : SyncRequest() {
        public enum class Action {
            ACCEPT, DELETE, BLOCK, BLOCK_AND_DELETE, UNBLOCK_AND_ACCEPT
        }
    }

    /**
     * Corresponds to constructing an instance of [JsonViewOnceOpenMessage]
     */
    public class ViewOnceOpened(public val sender: JsonAddress, public val messageTimestamp: Long) : SyncRequest() {
        /**
         * @throws IllegalArgumentException if the message is missing a source or a timestamp
         */
        public constructor(message: IncomingMessage) : this(message.data)

        /**
         * @throws IllegalArgumentException if the message is missing a source or a timestamp
         */
        public constructor(message: IncomingMessage.Data) : this(
            requireNotNull(message.source) { "message is missing a source" },
            requireNotNull(message.timestamp) { "message is missing a timestamp" }
        )
    }
}

/**
 * Used with the [org.inthewaves.kotlinsignald.Signal.updateGroup] function inside of [GroupUpdate]. A class to enforce
 * that only one of the access controls are updated at once.
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
 * Used with the [org.inthewaves.kotlinsignald.Signal.trust] function.
 */
public enum class TrustLevel {
    TRUSTED_UNVERIFIED, TRUSTED_VERIFIED, UNTRUSTED
}
