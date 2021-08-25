package org.inthewaves.kotlinsignald

import PersistentSocketWrapper
import SocketWrapper
import kotlinx.datetime.Clock
import org.inthewaves.kotlinsignald.Signal.Recipient.Group
import org.inthewaves.kotlinsignald.Signal.Recipient.Individual
import org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException
import org.inthewaves.kotlinsignald.clientprotocol.SignaldException
import org.inthewaves.kotlinsignald.clientprotocol.v0.structures.JsonAttachment
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.AcceptInvitationRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.Account
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.AccountList
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.AddLinkedDeviceRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.AddServerRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.AllIdentityKeyList
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ApproveMembershipRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ClientMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.CreateGroupRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.DeleteAccountRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.FinishLinkRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GenerateLinkingURIRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GetAllIdentities
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GetGroupRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GetIdentitiesRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GetLinkedDevicesRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GetProfileRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GetServersRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GroupAccessControl
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GroupInfo
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GroupLinkInfoRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GroupList
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GroupMember
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.IdentityKeyList
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JoinGroupRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonAddress
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonGroupJoinInfo
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonGroupV2Info
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonMention
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonQuote
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonReaction
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonVersionMessage
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.LeaveGroupRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.LinkedDevices
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.LinkingURI
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ListAccountsRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ListContactsRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ListGroupsRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.MarkReadRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.Payment
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.Profile
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ProfileList
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ReactRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.RegisterRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.RemoteDeleteRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.RemoveLinkedDeviceRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.RemoveServerRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.RequestSyncRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ResetSessionRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ResolveAddressRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.SendPaymentRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.SendRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.SendResponse
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.Server
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ServerList
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.SetDeviceNameRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.SetExpirationRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.SetProfile
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.SubscribeRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.TrustRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.TypingRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.UpdateContactRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.UpdateGroupRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.VerifyRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.VersionRequest
import org.inthewaves.kotlinsignald.subscription.BlockingMessageSubscriptionHandler

/**
 * A synchronous signald client, for use with V1 of the signald protocol. Note that the functions and the constructor
 * can block the thread tbat called the function / constructor, since it reads and writes responses from a UNIX socket.
 *
 * @constructor Creates a [Signal] instance for a particular account. A connection with the signald socket with be
 * attempted, throwing an exception if unable to connect to the socket.
 * @throws SocketUnavailableException if unable to connect to the socket
 * @throws SignaldException if unable to get list of accounts to cache current account data if already registered.
 * @param accountId See [accountId].
 * @param socketPath An optional path to the signald socket.
 */
public class Signal @Throws(SignaldException::class) constructor(
    /**
     * The ID of account corresponding to the signald account to use. As of the current version, this is
     * a phone number in E.164 format starting with a + character.
     */
    public val accountId: String,
    socketPath: String? = null
) {
    private val socketWrapper = SocketWrapper.create(socketPath)

    /**
     * The account info for the specified [accountId]. May be null if the account doesn't exist with signald.
     * If this is null, getting the account info will attempt a request to the signald socket.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming account list is invalid
     * @throws SignaldException if the request to the socket fails
     */
    public var accountInfo: Account? = getAccountOrNull()
        private set
        get() {
            if (field != null) {
                return field
            }
            val account = getAccountOrNull()
            if (account?.accountId == accountId) {
                field = account
            }
            return field
        }

    /**
     * Whether the [accountId] of this [Signal] instance is registered with signald. This will do a check with the
     * socket to ensure that the account is present with signald.
     *
     * Note that this does not detect registration with the Signal service itself. See
     * [https://gitlab.com/signald/signald/-/issues/192] for an issue about checking login status with the Signal
     * service
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    public val isRegisteredWithSignald: Boolean
        get() {
            if (accountInfo == null) {
                return false
            }

            val newAccount = getAccountOrNull()
            return if (newAccount != null) {
                accountInfo = newAccount
                true
            } else {
                accountInfo = null
                false
            }
        }

    private fun getAccountOrNull() =
        ListAccountsRequest().submit(socketWrapper).accounts.find { it.accountId == accountId }

    private inline fun <T> withAccountOrThrow(block: (account: Account) -> T): T {
        val account = accountInfo
        if (account?.accountId == null) {
            throw SignaldException("$accountId is not registered with signald")
        }
        return block(account)
    }

    /**
     * Accept a group V2 invitation. Note that you must have a profile name set to join groups.
     *
     * @param groupID A base-64 encoded ID of the group.
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun acceptInvitation(
        groupID: String
    ): JsonGroupV2Info {
        withAccountOrThrow {
            return AcceptInvitationRequest(
                account = accountId,
                groupID = groupID
            ).submit(socketWrapper)
        }
    }

    /**
     * Adds a linked device to the current account.
     *
     * @param uri the `tsdevice:/` uri provided (typically in qr code form) by the new device. Example:
     * `tsdevice:/?uuid=jAaZ5lxLfh7zVw5WELd6-Q&pub_key=BfFbjSwmAgpVJBXUdfmSgf61eX3a%2Bq9AoxAVpl1HUap9`
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun addDevice(uri: String) {
        withAccountOrThrow {
            AddLinkedDeviceRequest(
                account = accountId,
                uri = uri
            ).submit(socketWrapper)
        }
    }

    /**
     * Adds a new server to connect to and returns the new server's UUID.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun addServer(server: Server): String {
        return AddServerRequest(server).submit(socketWrapper)
    }

    /**
     * Approves the [members] requests to join a V2 group with the given [groupID]. Returns the new group information.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun approveMembership(groupID: String, members: Iterable<JsonAddress>): JsonGroupV2Info {
        withAccountOrThrow {
            return ApproveMembershipRequest(
                account = accountId,
                groupID = groupID,
                members = members.toList()
            ).submit(socketWrapper)
        }
    }

    /**
     * Creates a new group and returns the information of the newly created group.
     *
     * @param members The members to include in the group.
     * @param title The title of the group.
     * @param avatar A link to an image on the local filesystem. Example: `/tmp/image.jpg`
     * @param timer The message expiration timer for the group.
     * @param memberRole The role of all members other than the group creator. Options are ADMINISTRATOR or DEFAULT
     * (case-insensitive). Example: "ADMINISTRATOR"
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun createGroup(
        members: Iterable<JsonAddress>,
        title: String,
        avatar: String? = null,
        timer: Int? = null,
        memberRole: String? = null,
    ): JsonGroupV2Info {
        withAccountOrThrow {
            return CreateGroupRequest(
                account = accountId,
                members = members.toList(),
                title = title,
                avatar = avatar,
                timer = timer,
                memberRole = memberRole
            ).submit(socketWrapper)
        }
    }

    /**
     * Deletes all account data signald has on disk, and optionally delete the account from the server as
     * well. Note that this is not "unlink" and will delete the entire account, even from a linked device.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun deleteAccount(alsoDeleteAccountOnServer: Boolean) {
        withAccountOrThrow {
            DeleteAccountRequest(
                account = accountId,
                server = alsoDeleteAccountOnServer
            )
        }
    }

    /**
     * Deletes a previously added server from [addServer].
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun deleteServer(serverUuid: String) {
        withAccountOrThrow {
            RemoveServerRequest(uuid = serverUuid)
        }
    }

    /**
     * After a linking URI has been requested, finish_link must be called with the session_id provided
     * with the URI.
     *
     * @return Information about the new account once the linking process is completed by the other device.
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun finishLink(deviceName: String, sessionId: String): Account {
        withAccountOrThrow {
            return FinishLinkRequest(deviceName = deviceName, sessionId = sessionId).submit(socketWrapper)
        }
    }

    /**
     * Generate a linking URI. Typically, this is QR encoded and scanned by the primary device. Submit
     * the returned [LinkingURI.sessionId] with a [finishLink] request.
     *
     * @param serverUuid The identifier of the server to use. Leave `null` for default (usually Signal production
     * servers but configurable at build time)
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun generateLinkingUri(serverUuid: String? = null): LinkingURI {
        withAccountOrThrow {
            return GenerateLinkingURIRequest(server = serverUuid).submit(socketWrapper)
        }
    }

    /**
     * Returns all known identity keys for the current account
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun getAllIdentities(): AllIdentityKeyList {
        withAccountOrThrow {
            return GetAllIdentities(account = accountId).submit(socketWrapper)
        }
    }

    /**
     * Query the server for the latest state of a known group. If no account in signald is a member of the group
     * (anymore), a [RequestFailedException] with [RequestFailedException.errorType]: 'UnknownGroupException' is
     * returned.
     *
     * @param revision The latest known revision of the group, default value (-1) forces fetch from server
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun getGroup(groupID: String, revision: Int? = null): JsonGroupV2Info {
        withAccountOrThrow {
            return GetGroupRequest(account = accountId, groupID = groupID, revision = revision).submit(socketWrapper)
        }
    }

    /**
     * Get information about known identity keys for a particular [address].
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun getIdentities(address: JsonAddress): IdentityKeyList {
        withAccountOrThrow {
            return GetIdentitiesRequest(account = accountId, address = address).submit(socketWrapper)
        }
    }

    /**
     * Gets a list of all linked devices for this account.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun getLinkedDevices(): LinkedDevices {
        withAccountOrThrow {
            return GetLinkedDevicesRequest(account = accountId).submit(socketWrapper)
        }
    }

    /**
     * Gets all information available about a user
     *
     * @param address The address of the user to get the profile of.
     * @param async If true, return results from local store immediately, refreshing from server in the
     * background if needed. If false (default), block until profile can be retrieved from server
     */
    @Throws(SignaldException::class)
    public fun getProfile(address: JsonAddress, async: Boolean = false): Profile {
        withAccountOrThrow {
            return GetProfileRequest(account = accountId, async = async, address = address).submit(socketWrapper)
        }
    }

    /**
     * Gets a list of all signald servers
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun getServers(): ServerList = GetServersRequest().submit(socketWrapper)

    /**
     * Get information about a group from a `signal.group` [groupLink].
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun getGroupLinkInfo(groupLink: String): JsonGroupJoinInfo {
        withAccountOrThrow {
            return GroupLinkInfoRequest(account = accountId, uri = groupLink).submit(socketWrapper)
        }
    }

    /**
     * Joins a group using the given `signal.group` [groupLink].
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun joinGroup(groupLink: String): JsonGroupJoinInfo {
        withAccountOrThrow {
            return JoinGroupRequest(account = accountId, uri = groupLink).submit(socketWrapper)
        }
    }

    /**
     * Leaves a group with the specified [groupID]
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun leaveGroup(groupID: String): GroupInfo {
        withAccountOrThrow {
            return LeaveGroupRequest(account = accountId, groupID = groupID).submit(socketWrapper)
        }
    }

    /**
     * Returns a list of [Account]s logged-in to signald.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun listAccounts(): AccountList {
        return ListAccountsRequest().submit(socketWrapper)
    }

    /**
     * Returns a list of contacts for this account.
     *
     * @param async Return results from local store immediately, refreshing from server afterward if needed. If
     * false (default), block until all pending profiles have been retrieved.
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun listContacts(async: Boolean? = null): ProfileList {
        withAccountOrThrow {
            return ListContactsRequest(account = accountId, async = async).submit(socketWrapper)
        }
    }

    /**
     * Returns a list of groups for this account.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun listGroups(): GroupList {
        withAccountOrThrow {
            return ListGroupsRequest(account = accountId).submit(socketWrapper)
        }
    }

    /**
     * Marks the given messages (represented as [timestamps]) as read. Note that messages are identified using their
     * timestamps.
     *
     * @param to The address that sent the messages that will be marked as read.
     * @param timestamps The timestamps of the messages we want to mark as read.
     * @param when The timestamp to use for when we mark as read. Defaults to the current system clock's timestamp.
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun markRead(
        to: JsonAddress,
        timestamps: Iterable<Long>,
        `when`: Long = Clock.System.now().toEpochMilliseconds()
    ) {
        withAccountOrThrow {
            MarkReadRequest(
                account = accountId,
                to = to,
                timestamps = timestamps.toList(),
                `when` = `when`
            ).submit(socketWrapper)
        }
    }

    /**
     * Reacts to a previous message.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun react(
        recipient: Recipient,
        reaction: JsonReaction,
        timestamp: Long = Clock.System.now().toEpochMilliseconds()
    ): SendResponse {
        withAccountOrThrow {
            val request = when (recipient) {
                is Recipient.Group -> ReactRequest(
                    username = accountId,
                    recipientGroupId = recipient.groupID,
                    reaction = reaction,
                    timestamp = timestamp
                )
                is Recipient.Individual -> ReactRequest(
                    username = accountId,
                    recipientAddress = recipient.address,
                    reaction = reaction,
                    timestamp = timestamp
                )
            }
            return request.submit(socketWrapper)
        }
    }

    /**
     * Begin the account registration process by requesting a phone number verification code. When the code is received,
     * submit it with a [verify] request.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun register(voice: Boolean = false, captcha: String? = null) {
        RegisterRequest(
            account = accountId,
            voice = voice,
            captcha = captcha
        ).submit(socketWrapper)
    }

    /**
     * Sends a remote delete message to delete a message that was previously sent to the given [recipient]
     * (group or individual address).
     *
     * @return The [SendResponse] of the remote delete message.
     * @param recipient The recipient that received that message that we are trying to delete.
     * @param timestampOfTarget The timestamp of the message to delete. Note that messages are identified by their
     * timestamp.
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun remoteDelete(recipient: Recipient, timestampOfTarget: Long): SendResponse {
        withAccountOrThrow {
            val request = when (recipient) {
                is Recipient.Group -> RemoteDeleteRequest(
                    account = accountId,
                    group = recipient.groupID,
                    timestamp = timestampOfTarget
                )
                is Recipient.Individual -> RemoteDeleteRequest(
                    account = accountId,
                    address = recipient.address,
                    timestamp = timestampOfTarget
                )
            }
            return request.submit(socketWrapper)
        }
    }

    /**
     * Remove a linked device from the Signal account. Only allowed when the local device id is 1
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun removeLinkedDevice(deviceId: Long) {
        withAccountOrThrow {
            // we could check the account here and bail out earlier
            RemoveLinkedDeviceRequest(
                account = accountId,
                deviceId = deviceId
            ).submit(socketWrapper)
        }
    }

    /**
     * Request other devices on the account send us their group list, syncable config, contact list, and block list.
     *
     * @param groups Request group list sync (default: true)
     * @param configuration Request configuration sync (default: true)
     * @param contacts Request contact list sync (default: true)
     * @param blocked Request block list sync (default: true)
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun requestSync(
        groups: Boolean = true,
        configuration: Boolean = true,
        contacts: Boolean = true,
        blocked: Boolean = true,
    ) {
        withAccountOrThrow {
            RequestSyncRequest(
                account = accountId,
                groups = groups,
                configuration = configuration,
                contacts = contacts,
                blocked = blocked,
            ).submit(socketWrapper)
        }
    }

    /**
     * Resets a secure session with a particular user identified by the given [address].
     *
     * @param address The user to reset session with
     * @param timestamp The timestamp to use when resetting session. Defaults to now.
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun resetSession(
        address: JsonAddress,
        timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    ): SendResponse {
        withAccountOrThrow {
            return ResetSessionRequest(
                account = accountId,
                address = address,
                timestamp = timestamp
            ).submit(socketWrapper)
        }
    }

    /**
     * Resolve a [partial] JsonAddress with only a number or UUID to one with both. Anywhere that signald
     * accepts a JsonAddress will accept a partial; this is a convenience function for client authors,
     * mostly because signald doesn't resolve all the partials it returns.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun resolveAddress(partial: JsonAddress): JsonAddress {
        withAccountOrThrow {
            return ResolveAddressRequest(
                account = accountId,
                partial = partial,
            ).submit(socketWrapper)
        }
    }

    /**
     * Sends a message to either a single user ([Recipient.Individual]) or a group ([Recipient.Group]).
     *
     * @param recipient The recipient that will receive our message. If sending to a group, note that signald will
     * handle the fan-out of messages to all users.
     * @param messageBody The body of the message.
     * @param attachments Attachments to include in the message.
     * @param quote A quote to include in the message, where the quote refers to a previous message.
     * @param mentions Mentions to include in the message. Typically, an empty space is used as the mention placeholder,
     * and then the position of the empty space is referred to by the [JsonMention.start] property.
     * @param timestamp The timestamp of the message that we are sending. Default to the current system clock time.
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun send(
        recipient: Recipient,
        messageBody: String,
        attachments: Iterable<JsonAttachment> = emptyList(),
        quote: JsonQuote? = null,
        mentions: Iterable<JsonMention> = emptyList(),
        timestamp: Long = Clock.System.now().toEpochMilliseconds()
    ): SendResponse {
        withAccountOrThrow {
            val request = when (recipient) {
                is Recipient.Group -> SendRequest(
                    username = accountId,
                    recipientGroupId = recipient.groupID,
                    messageBody = messageBody,
                    attachments = attachments.toList(),
                    quote = quote,
                    timestamp = timestamp,
                    mentions = mentions.toList(),
                )
                is Recipient.Individual -> SendRequest(
                    username = accountId,
                    recipientAddress = recipient.address,
                    messageBody = messageBody,
                    attachments = attachments.toList(),
                    quote = quote,
                    timestamp = timestamp,
                    mentions = mentions.toList(),
                )
            }
            return request.submit(socketWrapper)
        }
    }

    /**
     * Sends a MobileCoin [payment] to the user identified by the [recipientAddress].
     *
     * @param recipientAddress The user that will receive the payment.
     * @param payment The payment to make.
     * @param when The timestamp of the payment. Defaults to the current system clock time.
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun sendPayment(
        recipientAddress: JsonAddress,
        payment: Payment,
        `when`: Long = Clock.System.now().toEpochMilliseconds(),
    ): SendResponse {
        withAccountOrThrow {
            return SendPaymentRequest(
                account = accountId,
                address = recipientAddress,
                payment = payment,
                `when` = `when`
            ).submit(socketWrapper)
        }
    }

    /**
     * Set this device's name. This will show up on the mobile device on the same account under linked devices
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun setDeviceName(deviceName: String) {
        withAccountOrThrow {
            SetDeviceNameRequest(
                account = accountId,
                deviceName = deviceName
            ).submit(socketWrapper)
        }
    }

    /**
     * Set the message expiration timer for a thread.
     *
     * @param recipient The recipient to set the [expiration] timer for.
     * @param expiration The new expiration timer in seconds. Set to 0 to disable timer. Example: 604800
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun setExpiration(recipient: Recipient, expiration: Int): SendResponse {
        withAccountOrThrow {
            val request = when (recipient) {
                is Recipient.Group -> SetExpirationRequest(
                    account = accountId,
                    group = recipient.groupID,
                    expiration = expiration,
                )
                is Recipient.Individual -> SetExpirationRequest(
                    account = accountId,
                    address = recipient.address,
                    expiration = expiration,
                )
            }
            return request.submit(socketWrapper)
        }
    }

    /**
     * Sets the profile of the current account. Note that all the parameters here will be used as the new profile
     * fields; leaving one of the fields unset or null means it will be treated as clearing it.
     *
     * @param name A new profile name. Set to empty string for no profile name Example: "signald user"
     * @param avatarFile Path to new profile avatar file. If unset or null, this will unset the profile avatar.
     * Example: "/tmp/image.jpg
     * @param about An optional about string. If unset, null or an empty string will unset profile's about field.
     * @param emoji An optional single emoji character. If unset, null or an empty string will unset profile emoji.
     * @param mobileCoinAddress an optional base64-encoded MobileCoin address to set in the profile. Note that this is
     * not the traditional MobileCoin address encoding, which is custom. Clients are responsible for converting between
     * MobileCoin's custom base58 on the user-facing side and base64 encoding on the signald side. If unset, null or an
     * empty string, will empty the profile payment address
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun setProfile(
        name: String,
        avatarFile: String?,
        about: String?,
        emoji: String?,
        mobileCoinAddress: String?
    ) {
        withAccountOrThrow {
            SetProfile(
                account = accountId,
                name = name,
                avatarFile = avatarFile,
                about = about,
                emoji = emoji,
                mobilecoinAddress = mobileCoinAddress
            ).submit(socketWrapper)
        }
    }

    /**
     * Used with the [trust] function.
     */
    public enum class TrustLevel {
        TRUSTED_UNVERIFIED, TRUSTED_VERIFIED, UNTRUSTED
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
     * Trust another user's safety number using either the QR code data or the safety number text
     *
     * @param address The user to trust
     * @param fingerprint The fingerprint to use for trusting the user. Either use their safety number
     * ([Fingerprint.SafetyNumber]) or base64-encoded QR code data ([Fingerprint.QrCodeData]).
     * @param trustLevel One of TRUSTED_UNVERIFIED, TRUSTED_VERIFIED or UNTRUSTED. Default is TRUSTED_VERIFIED
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun trust(
        address: JsonAddress,
        fingerprint: Fingerprint,
        trustLevel: TrustLevel = TrustLevel.TRUSTED_VERIFIED
    ) {
        withAccountOrThrow {
            val request = when (fingerprint) {
                is Fingerprint.SafetyNumber -> TrustRequest(
                    account = accountId,
                    address = address,
                    safetyNumber = fingerprint.safetyNumber,
                    trustLevel = trustLevel.name
                )
                is Fingerprint.QrCodeData -> TrustRequest(
                    account = accountId,
                    address = address,
                    qrCodeData = fingerprint.qrCodeData,
                    trustLevel = trustLevel.name
                )
            }
            request.submit(socketWrapper)
        }
    }

    /**
     * Send a typing started or stopped message
     *
     * @param recipient The recipient to send the typing message to.
     * @param isTyping Whether the message should be a typing started (true) or typing stopped (false) message.
     * @param when The timestamp of the typing message. Defaults to the current system clock time.
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun typing(
        recipient: Recipient,
        isTyping: Boolean,
        `when`: Long = Clock.System.now().toEpochMilliseconds()
    ) {
        withAccountOrThrow {
            val request = when (recipient) {
                is Recipient.Group -> TypingRequest(
                    account = accountId,
                    group = recipient.groupID,
                    typing = isTyping,
                    `when` = `when`
                )
                is Recipient.Individual -> TypingRequest(
                    account = accountId,
                    address = recipient.address,
                    typing = isTyping,
                    `when` = `when`
                )
            }
            request.submit(socketWrapper)
        }
    }

    /**
     * Update information about a local contact. Null properties will be left alone.
     *
     * @param address Address of the local contact to update.
     * @param name The new name of the contact
     * @param color The new color for the contact.
     * @param inboxPosition The new inbox position for the contact.
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun updateContact(
        address: JsonAddress,
        name: String? = null,
        color: String? = null,
        inboxPosition: Int? = null,
    ) {
        withAccountOrThrow {
            UpdateContactRequest(
                account = accountId,
                address = address,
                name = name,
                color = color,
                inboxPosition = inboxPosition
            ).submit(socketWrapper)
        }
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
     * Used with the [updateGroup] function.
     */
    public enum class GroupLinkStatus {
        OFF, ON_NO_ADMIN_APPROVAL, ON_WITH_ADMIN_APPROVAL
    }

    /**
     * Used with the [updateGroup] function. class to enforce that only one of the group attributes are updated at once.
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
        public object ResetLink : GroupUpdate
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
     * Update information about a group
     *
     * @param groupID The base64-encoded ID of the V2 group to update
     * @param groupUpdate The update to make to the group. This is done so that only one of the modification actions can
     * be performed at once. See [GroupUpdate] for details.
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun updateGroup(groupID: String, groupUpdate: GroupUpdate): GroupInfo {
        withAccountOrThrow {
            val request = when (groupUpdate) {
                is GroupUpdate.Title -> UpdateGroupRequest(
                    account = accountId,
                    groupID = groupID,
                    title = groupUpdate.newTitle
                )
                is GroupUpdate.Description -> UpdateGroupRequest(
                    account = accountId,
                    groupID = groupID,
                    description = groupUpdate.newDescription
                )
                is GroupUpdate.Avatar -> UpdateGroupRequest(
                    account = accountId,
                    groupID = groupID,
                    avatar = groupUpdate.newAvatarPath
                )
                is GroupUpdate.UpdateExpirationTimer -> UpdateGroupRequest(
                    account = accountId,
                    groupID = groupID,
                    updateTimer = groupUpdate.newTimerSeconds
                )
                is GroupUpdate.AddMembers -> UpdateGroupRequest(
                    account = accountId,
                    groupID = groupID,
                    addMembers = groupUpdate.membersToAdd.toList()
                )
                is GroupUpdate.RemoveMembers -> UpdateGroupRequest(
                    account = accountId,
                    groupID = groupID,
                    removeMembers = groupUpdate.membersToRemove.toList()
                )
                is GroupUpdate.UpdateRole -> UpdateGroupRequest(
                    account = accountId,
                    groupID = groupID,
                    updateRole = groupUpdate.memberWithUpdatedRole
                )
                is GroupUpdate.UpdateAccessControl -> UpdateGroupRequest(
                    account = accountId,
                    groupID = groupID,
                    updateAccessControl = groupUpdate.groupAccessControl
                )
                is GroupUpdate.ResetLink -> UpdateGroupRequest(
                    account = accountId,
                    groupID = groupID,
                    resetLink = true
                )
            }
            return request.submit(socketWrapper)
        }
    }

    /**
     * Verify an account's phone number with a code after registering, completing the account creation
     * process.
     *
     * @param code The verification code from SMS or voice call.
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun verify(code: String) {
        val account = VerifyRequest(accountId, code).submit(socketWrapper)
        accountInfo = account
    }

    /**
     * Gets the version of signald
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    @Throws(SignaldException::class)
    public fun version(): JsonVersionMessage = VersionRequest().submit(socketWrapper)

    /**
     * Receive incoming messages by creating a new, dedicated socket connection. After making a subscribe request,
     * incoming messages will be sent to the client encoded as [ClientMessageWrapper]. Send an unsubscribe request via
     * [Subscription.unsubscribe] or disconnect from the socket via [PersistentSocketWrapper.close] to stop receiving
     * messages.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    internal fun subscribe(): Subscription {
        withAccountOrThrow {
            val persistentSocket = PersistentSocketWrapper.create(socketWrapper.actualSocketPath)
            try {
                val subscribeResponse = SubscribeRequest(account = accountId).submit(persistentSocket)
                return Subscription(accountId = accountId, persistentSocket, subscribeResponse.messages)
            } catch (e: Throwable) {
                try {
                    persistentSocket.close()
                } catch (closeError: Throwable) {
                    e.addSuppressed(closeError)
                }
                throw e
            }
        }
    }

    /**
     * Subscribes to incoming messages and consumes them on the thread that called this function. This will open a
     * dedicated, persistent socket connection for this function call. The current thread will be blocked when it waits
     * for more messages. After this function executes, an unsubscribe request will be made and the persistent socket
     * will be closed.
     *
     * @throws RequestFailedException if the subscribe request fails
     * @throws SignaldException if an I/O error occurs when communicating to the socket
     */
    @Throws(SignaldException::class)
    public fun subscribeAndConsumeBlocking(messageConsumer: (ClientMessageWrapper) -> Unit) {
        val subscriptionHandler = BlockingMessageSubscriptionHandler(this)
        try {
            subscriptionHandler.consumeEach(messageConsumer)
        } finally {
            subscriptionHandler.close()
        }
    }

    /**
     * Specifies a recipient type for functions that can send to either type (e.g., [react], [remoteDelete], [send],
     * [setExpiration], [typing]). Recipients are either to a [Group] or an [Individual].
     */
    public sealed class Recipient {
        /**
         * A group as the recipient, using the base64-encoded [groupID] as the identifier. [groupID]s can be retrieved
         * with [listGroups] or by reading the responses from [joinGroup] or [getGroupLinkInfo].
         */
        public class Group(public val groupID: String) : Recipient()
        /**
         * A Signal user as the recipient.
         */
        public class Individual(public val address: JsonAddress) : Recipient()
    }
}
