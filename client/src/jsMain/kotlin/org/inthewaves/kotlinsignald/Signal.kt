package org.inthewaves.kotlinsignald

import kotlinx.datetime.Clock
import org.inthewaves.kotlinsignald.Signal.Companion.create
import org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException
import org.inthewaves.kotlinsignald.clientprotocol.SignaldException
import org.inthewaves.kotlinsignald.clientprotocol.v0.structures.JsonAttachment
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.AcceptInvitationRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.Account
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.AccountAlreadyVerifiedError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.AccountHasNoKeysError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.AccountList
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.AccountLockedError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.AddLinkedDeviceRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.AddServerRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.AllIdentityKeyList
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ApproveMembershipRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.CaptchaRequiredError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.CreateGroupRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.DeleteAccountRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.FingerprintVersionMismatchError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.FinishLinkRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GenerateLinkingURIRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GetAllIdentities
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GetGroupRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GetIdentitiesRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GetLinkedDevicesRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GetProfileRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GetServersRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GroupInfo
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GroupLinkInfoRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GroupLinkNotActiveError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GroupList
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GroupNotActiveError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GroupVerificationError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.IdentityKeyList
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.InternalError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.InvalidAttachmentError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.InvalidBase64Error
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.InvalidFingerprintError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.InvalidGroupError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.InvalidGroupStateError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.InvalidInviteURIError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.InvalidProxyError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.InvalidRecipientError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.InvalidRequestError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JoinGroupRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonAddress
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonGroupJoinInfo
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonGroupV2Info
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonMention
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonPreview
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
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.NoKnownUUIDError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.NoSendPermissionError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.NoSuchAccountError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.OwnProfileKeyDoesNotExistError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.Payment
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.Profile
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ProfileList
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ProfileUnavailableError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.RateLimitError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ReactRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.RefuseMembershipRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.RegisterRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.RemoteConfigList
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.RemoteConfigRequest
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
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ServerNotFoundError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.SetDeviceNameRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.SetExpirationRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.SetProfile
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.SubmitChallengeRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.SubscribeRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.TrustRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.TypingRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.UnknownGroupError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.UnknownIdentityKeyError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.UntrustedIdentityError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.UpdateContactRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.UpdateGroupRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.UserAlreadyExistsError
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.VerifyRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.VersionRequest
import org.inthewaves.kotlinsignald.subscription.Subscription

/**
 * An asynchronous signald client, for use with V1 of the signald protocol. Use the [create] function to create an
 * instance.
 *
 * @throws SocketUnavailableException if unable to connect to the socket
 * @throws SignaldException if unable to get list of accounts to cache current account data if already registered.
 * @param accountId See [accountId].
 * @param socketPath An optional path to the signald socket.
 */
public actual class Signal private constructor(
    /**
     * The ID of account corresponding to the signald account to use. As of the current version, this is
     * a phone number in E.164 format starting with a + character.
     */
    public actual val accountId: String,
    private val socketWrapper: NodeSocketWrapper,
) : SignaldClient {
    /**
     * The account info for the specified [accountId]. May be null if the account doesn't exist with signald.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming account list is invalid
     * @throws SignaldException if the request to the socket fails
     */
    private var accountInfo: Account? = null

    public suspend fun getAccountInfo(forceUpdate: Boolean = false): Account? {
        val accountInfoNow = accountInfo
        if (accountInfoNow != null && !forceUpdate) {
            return accountInfoNow
        }

        val newAccountInfo = getAccountOrNull()
        accountInfo = newAccountInfo
        return newAccountInfo
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
    public suspend fun isRegisteredWithSignald(): Boolean = getAccountInfo(forceUpdate = true) != null

    private suspend fun getAccountOrNull() =
        ListAccountsRequest().submitSuspend(socketWrapper).accounts.find { it.accountId == accountId }

    private suspend inline fun <T> withAccountOrThrow(block: (account: Account) -> T): T {
        val account = getAccountInfo(forceUpdate = false)
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
     * @throws NoSuchAccountError
     * @throws OwnProfileKeyDoesNotExistError
     * @throws ServerNotFoundError
     * @throws InvalidProxyError
     * @throws UnknownGroupError
     * @throws InternalError
     * @throws InvalidRequestError
     */
    public suspend fun acceptInvitation(
        groupID: String
    ): JsonGroupV2Info {
        withAccountOrThrow {
            return AcceptInvitationRequest(
                account = accountId,
                groupID = groupID
            ).submitSuspend(socketWrapper)
        }
    }

    /**
     * Adds a linked device to the current account.
     *
     * @param uri the `tsdevice:/` uri provided (typically in qr code form) by the new device. Example:
     * `tsdevice:/?uuid=jAaZ5lxLfh7zVw5WELd6-Q&pub_key=BfFbjSwmAgpVJBXUdfmSgf61eX3a%2Bq9AoxAVpl1HUap9`
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws NoSuchAccountError
     * @throws ServerNotFoundError
     * @throws InvalidProxyError
     * @throws InvalidRequestError caused by syntax errors with the provided linking URI
     * @throws InternalError
     */
    public suspend fun addDevice(uri: String) {
        withAccountOrThrow {
            AddLinkedDeviceRequest(
                account = accountId,
                uri = uri
            ).submitSuspend(socketWrapper)
        }
    }

    /**
     * Adds a new server to connect to and returns the new server's UUID.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws InvalidProxyError
     * @throws InternalError
     */
    public suspend fun addServer(server: Server): String {
        return AddServerRequest(server).submitSuspend(socketWrapper)
    }

    /**
     * Approves the [members] requests to join a V2 group with the given [groupID]. Returns the new group information.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws NoSuchAccountError
     * @throws ServerNotFoundError
     * @throws InvalidProxyError
     * @throws UnknownGroupError
     * @throws InternalError
     * @throws GroupVerificationError
     * @throws InvalidRequestError
     */
    public suspend fun approveMembership(groupID: String, members: Collection<JsonAddress>): JsonGroupV2Info {
        withAccountOrThrow {
            return ApproveMembershipRequest(
                account = accountId,
                groupID = groupID,
                members = members.toList()
            ).submitSuspend(socketWrapper)
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
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     * @throws OwnProfileKeyDoesNotExistError
     * @throws NoKnownUUIDError
     * @throws InvalidRequestError
     * @throws GroupVerificationError
     * @throws InvalidGroupStateError
     * @throws UnknownGroupError
     */
    public suspend fun createGroup(
        members: Collection<JsonAddress>,
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
            ).submitSuspend(socketWrapper)
        }
    }

    /**
     * Deletes all account data signald has on disk, and optionally delete the account from the server as
     * well. Note that this is not "unlink" and will delete the entire account, even from a linked device.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     */
    public suspend fun deleteAccount(alsoDeleteAccountOnServer: Boolean) {
        withAccountOrThrow {
            DeleteAccountRequest(
                account = accountId,
                server = alsoDeleteAccountOnServer
            ).submitSuspend(socketWrapper)
        }
    }

    /**
     * Deletes a previously added server from [addServer].
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws InternalError
     */
    public suspend fun deleteServer(serverUuid: String) {
        withAccountOrThrow {
            RemoveServerRequest(uuid = serverUuid).submitSuspend(socketWrapper)
        }
    }

    /**
     * After a linking URI has been requested, finish_link must be called with the session_id provided
     * with the URI.
     *
     * @return Information about the new account once the linking process is completed by the other device.
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * throws NoSuchSessionError
     * @throws ServerNotFoundError
     * @throws InvalidProxyError
     * @throws InternalError
     * @throws NoSuchAccountError
     * @throws UserAlreadyExistsError
     */
    public suspend fun finishLink(deviceName: String, sessionId: String): Account {
        withAccountOrThrow {
            return FinishLinkRequest(deviceName = deviceName, sessionId = sessionId).submitSuspend(socketWrapper)
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
     * @throws ServerNotFoundError
     * @throws InvalidProxyError
     */
    public suspend fun generateLinkingUri(serverUuid: String? = null): LinkingURI {
        withAccountOrThrow {
            return GenerateLinkingURIRequest(server = serverUuid).submitSuspend(socketWrapper)
        }
    }

    /**
     * Returns all known identity keys for the current account
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws InvalidProxyError
     * @throws NoSuchAccountError
     * @throws ServerNotFoundError
     * @throws InternalError
     */
    public suspend fun getAllIdentities(): AllIdentityKeyList {
        withAccountOrThrow {
            return GetAllIdentities(account = accountId).submitSuspend(socketWrapper)
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
     * @throws NoSuchAccountError
     * @throws UnknownGroupError
     * @throws ServerNotFoundError
     * @throws InvalidProxyError
     * @throws InternalError
     * @throws GroupVerificationError
     * @throws InvalidGroupStateError
     * @throws InvalidRequestError
     */
    public suspend fun getGroup(groupID: String, revision: Int? = null): JsonGroupV2Info {
        withAccountOrThrow {
            return GetGroupRequest(account = accountId, groupID = groupID, revision = revision).submitSuspend(
                socketWrapper
            )
        }
    }

    /**
     * Get information about known identity keys for a particular [address].
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     */
    public suspend fun getIdentities(address: JsonAddress): IdentityKeyList {
        withAccountOrThrow {
            return GetIdentitiesRequest(account = accountId, address = address).submitSuspend(socketWrapper)
        }
    }

    /**
     * Gets a list of all linked devices for this account.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     * @throws InvalidRequestError
     */
    public suspend fun getLinkedDevices(): LinkedDevices {
        withAccountOrThrow {
            return GetLinkedDevicesRequest(account = accountId).submitSuspend(socketWrapper)
        }
    }

    /**
     * Gets all information available about a user
     *
     * @param address The address of the user to get the profile of.
     * @param async If true, return results from local store immediately, refreshing from server in the
     * background if needed. If false (default), block until profile can be retrieved from server
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     * @throws ProfileUnavailableError
     */
    public suspend fun getProfile(address: JsonAddress, async: Boolean = false): Profile {
        withAccountOrThrow {
            return GetProfileRequest(account = accountId, async = async, address = address).submitSuspend(socketWrapper)
        }
    }

    /**
     * Gets a list of all signald servers
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws InternalError
     */
    public suspend fun getServers(): ServerList = GetServersRequest().submitSuspend(socketWrapper)

    /**
     * Get information about a group from a `signal.group` [groupLink].
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws GroupLinkNotActiveError
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     * @throws InvalidRequestError
     * @throws GroupVerificationError
     */
    public suspend fun getGroupLinkInfo(groupLink: String): JsonGroupJoinInfo {
        withAccountOrThrow {
            return GroupLinkInfoRequest(account = accountId, uri = groupLink).submitSuspend(socketWrapper)
        }
    }

    /**
     * Joins a group using the given `signal.group` [groupLink].
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws InvalidRequestError
     * @throws InvalidInviteURIError
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     * @throws OwnProfileKeyDoesNotExistError
     * @throws GroupVerificationError
     * @throws GroupNotActiveError
     * @throws UnknownGroupError
     * @throws InvalidGroupStateError
     */
    public suspend fun joinGroup(groupLink: String): JsonGroupJoinInfo {
        withAccountOrThrow {
            return JoinGroupRequest(account = accountId, uri = groupLink).submitSuspend(socketWrapper)
        }
    }

    /**
     * Leaves a group with the specified [groupID]
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws NoSuchAccountError
     * @throws ServerNotFoundError
     * @throws InvalidProxyError
     * @throws InternalError
     * @throws UnknownGroupError
     * @throws GroupVerificationError
     * @throws InvalidRequestError
     */
    public suspend fun leaveGroup(groupID: String): GroupInfo {
        withAccountOrThrow {
            return LeaveGroupRequest(account = accountId, groupID = groupID).submitSuspend(socketWrapper)
        }
    }

    /**
     * Returns a list of [Account]s logged-in to signald.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws NoSuchAccountError
     * @throws InternalError
     */
    public suspend fun listAccounts(): AccountList {
        return ListAccountsRequest().submitSuspend(socketWrapper)
    }

    /**
     * Returns a list of contacts for this account.
     *
     * @param async Return results from local store immediately, refreshing from server afterward if needed. If
     * false (default), block until all pending profiles have been retrieved.
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    public suspend fun listContacts(async: Boolean? = null): ProfileList {
        withAccountOrThrow {
            return ListContactsRequest(account = accountId, async = async).submitSuspend(socketWrapper)
        }
    }

    /**
     * Returns a list of groups for this account.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     */
    public suspend fun listGroups(): GroupList {
        withAccountOrThrow {
            return ListGroupsRequest(account = accountId).submitSuspend(socketWrapper)
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
     * @throws NoSuchAccountError
     * @throws ServerNotFoundError
     * @throws InvalidProxyError
     * @throws InternalError
     * @throws UntrustedIdentityError
     */
    public suspend fun markRead(
        to: JsonAddress,
        timestamps: Collection<Long>,
        `when`: Long = Clock.System.now().toEpochMilliseconds()
    ) {
        withAccountOrThrow {
            MarkReadRequest(
                account = accountId,
                to = to,
                timestamps = timestamps.toList(),
                `when` = `when`
            ).submitSuspend(socketWrapper)
        }
    }

    /**
     * Reacts to a previous message.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws NoSuchAccountError
     * @throws ServerNotFoundError
     * @throws InvalidProxyError
     * @throws NoSendPermissionError
     * @throws InternalError
     * @throws InvalidRecipientError
     * @throws UnknownGroupError
     * @throws InvalidRequestError
     * @throws RateLimitError
     */
    public suspend fun react(
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
            return request.submitSuspend(socketWrapper)
        }
    }

    /**
     * Deny requests from users from joining a group.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws NoSuchAccountError
     * @throws ServerNotFoundError
     * @throws InvalidProxyError
     * @throws UnknownGroupError
     * @throws GroupVerificationError
     * @throws InternalError
     * @throws InvalidRequestError
     */
    public suspend fun refuseMembership(groupID: String, members: Collection<JsonAddress>): JsonGroupV2Info {
        withAccountOrThrow {
            return RefuseMembershipRequest(
                account = accountId,
                members = members.toList(),
                groupId = groupID
            ).submitSuspend(socketWrapper)
        }
    }

    /**
     * Begin the account registration process by requesting a phone number verification code. When the code is received,
     * submit it with a [verify] request.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws CaptchaRequiredError
     * @throws ServerNotFoundError
     * @throws InvalidProxyError
     */
    public suspend fun register(voice: Boolean = false, captcha: String? = null) {
        RegisterRequest(
            account = accountId,
            voice = voice,
            captcha = captcha
        ).submitSuspend(socketWrapper)
    }

    /**
     * Gets the remote config (feature flags) from the server.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     */
    public suspend fun remoteConfig(): RemoteConfigList {
        withAccountOrThrow {
            return RemoteConfigRequest(account = accountId).submitSuspend(socketWrapper)
        }
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
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     * @throws InvalidRecipientError
     * @throws NoSendPermissionError
     * @throws UnknownGroupError
     * @throws InvalidRequestError
     * @throws RateLimitError
     */
    public suspend fun remoteDelete(recipient: Recipient, timestampOfTarget: Long): SendResponse {
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
            return request.submitSuspend(socketWrapper)
        }
    }

    /**
     * Remove a linked device from the Signal account. Only allowed when the local device id is 1
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     *  @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     * @throws InvalidRequestError
     */
    public suspend fun removeLinkedDevice(deviceId: Long) {
        withAccountOrThrow {
            // we could check the account here and bail out earlier
            RemoveLinkedDeviceRequest(
                account = accountId,
                deviceId = deviceId
            ).submitSuspend(socketWrapper)
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
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     * @throws UntrustedIdentityError
     */
    public suspend fun requestSync(
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
            ).submitSuspend(socketWrapper)
        }
    }

    /**
     * Resets a secure session with a particular user identified by the given [address].
     *
     * @param address The user to reset session with
     * @param timestamp The timestamp to use when resetting session. Defaults to now.
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws InternalError
     * @throws ServerNotFoundError
     * @throws InvalidProxyError
     * @throws NoSuchAccountError
     * @throws InvalidRequestError
     * @throws NoSendPermissionError
     * @throws UnknownGroupError
     * @throws RateLimitError
     * @throws InvalidRecipientError
     */
    public suspend fun resetSession(
        address: JsonAddress,
        timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    ): SendResponse {
        withAccountOrThrow {
            return ResetSessionRequest(
                account = accountId,
                address = address,
                timestamp = timestamp
            ).submitSuspend(socketWrapper)
        }
    }

    /**
     * Resolve a [partial] JsonAddress with only a number or UUID to one with both. Anywhere that signald
     * accepts a JsonAddress will accept a partial; this is a convenience function for client authors,
     * mostly because signald doesn't resolve all the partials it returns.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws InternalError
     * @throws NoSuchAccountError
     */
    public suspend fun resolveAddress(partial: JsonAddress): JsonAddress {
        withAccountOrThrow {
            return ResolveAddressRequest(
                account = accountId,
                partial = partial,
            ).submitSuspend(socketWrapper)
        }
    }

    /**
     * Sends a message to either a single user ([Recipient.Individual]) or a group ([Recipient.Group]).
     *
     * @param recipient The recipient that will receive our message. If sending to a group, note that signald will
     * handle the fan-out of messages to all users.
     * @param messageBody The body of the message.
     * @param timestamp The timestamp of the message that we are sending. Default to the current system clock time.
     * @param attachments Attachments to include in the message.
     * @param quote A quote to include in the message, where the quote refers to a previous message.
     * @param mentions Mentions to include in the message. Typically, an empty space is used as the mention placeholder,
     * and then the position of the empty space is referred to by the [JsonMention.start] property.
     * @param previews Link previews to include in the message.
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws NoSuchAccountError
     * @throws ServerNotFoundError
     * @throws InvalidProxyError
     * @throws NoSendPermissionError
     * @throws InvalidAttachmentError
     * @throws InternalError
     * @throws InvalidRequestError
     * @throws UnknownGroupError
     * @throws RateLimitError
     * @throws InvalidRecipientError
     */
    public suspend fun send(
        recipient: Recipient,
        messageBody: String,
        timestamp: Long = Clock.System.now().toEpochMilliseconds(),
        attachments: Collection<JsonAttachment> = emptyList(),
        quote: JsonQuote? = null,
        mentions: Collection<JsonMention> = emptyList(),
        previews: Collection<JsonPreview> = emptyList(),
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
                    previews = previews.toList(),
                )
                is Recipient.Individual -> SendRequest(
                    username = accountId,
                    recipientAddress = recipient.address,
                    messageBody = messageBody,
                    attachments = attachments.toList(),
                    quote = quote,
                    timestamp = timestamp,
                    mentions = mentions.toList(),
                    previews = previews.toList(),
                )
            }
            return request.submitSuspend(socketWrapper)
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
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     * @throws InvalidBase64Error
     * @throws InvalidRecipientError
     * @throws UnknownGroupError
     * @throws NoSendPermissionError
     * @throws InvalidRequestError
     * @throws RateLimitError
     */
    public suspend fun sendPayment(
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
            ).submitSuspend(socketWrapper)
        }
    }

    /**
     * Set this device's name. This will show up on the mobile device on the same account under linked devices
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     */
    public suspend fun setDeviceName(deviceName: String) {
        withAccountOrThrow {
            SetDeviceNameRequest(
                account = accountId,
                deviceName = deviceName
            ).submitSuspend(socketWrapper)
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
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     * @throws UnknownGroupError
     * @throws GroupVerificationError
     * @throws InvalidRequestError
     */
    public suspend fun setExpiration(recipient: Recipient, expiration: Int): SendResponse {
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
            return request.submitSuspend(socketWrapper)
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
     * @param mobileCoinAddress An optional base64-encoded MobileCoin address to set in the profile. Payment address
     * must be a *base64-encoded* MobileCoin address. Note that this is not the traditional MobileCoin address encoding,
     * which is custom. Clients are responsible for converting between MobileCoin's custom base58 on the user-facing
     * side and base64 encoding on the signald side.
     * @param visibleBadgeIds IDs of badges. Badges and their IDs are dynamically provided by the server.
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     * @throws InvalidBase64Error
     * @throws InvalidRequestError
     */
    public suspend fun setProfile(
        name: String,
        avatarFile: String?,
        about: String?,
        emoji: String?,
        mobileCoinAddress: String?,
        visibleBadgeIds: List<String>?
    ) {
        withAccountOrThrow {
            SetProfile(
                account = accountId,
                name = name,
                avatarFile = avatarFile,
                about = about,
                emoji = emoji,
                mobilecoinAddress = mobileCoinAddress,
                visibleBadgeIds = visibleBadgeIds
            ).submitSuspend(socketWrapper)
        }
    }

    override fun subscribe(): IncomingMessageSubscription {
        throw UnsupportedOperationException("subscribe not supported on JS")
    }

    /**
     * Receive incoming messages by creating a new, dedicated socket connection. After making a subscribe request,
     * incoming messages will be sent to the client encoded as [ClientMessageWrapper]. Send an unsubscribe request via
     * [Subscription.unsubscribe] or disconnect from the socket via [NodePersistentSocketWrapper.close] to stop receiving
     * messages.
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws NoSuchAccountError
     * @throws ServerNotFoundError
     * @throws InvalidProxyError
     * @throws InternalError
     */
    override suspend fun subscribeSuspend(): Subscription {
        withAccountOrThrow {
            val persistentSocket = NodePersistentSocketWrapper.createSuspend(socketWrapper.actualSocketPath)
            try {
                val subscribeResponse = SubscribeRequest(account = accountId).submitSuspend(persistentSocket)
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
     * Submits a challenge that is requested by the server. Sometimes, when sending a message, the server may rate
     * limit the sender (signald has `ProofRequiredError`), requiring the user to either submit a push challenge (on
     * Android, this is via Firebase Cloud Message) or a reCAPTCHA. This is indicated by the property
     * [org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonSendMessageResult.proofRequiredFailure], which
     * corresponds to `ProofRequiredException` in the Signal-Android code.
     *
     * @throws NoSuchAccountError
     * @throws InvalidRequestError
     * @throws ServerNotFoundError
     * @throws InvalidProxyError
     * @throws InternalError
     * @see RateLimitChallenge
     */
    public suspend fun submitChallenge(challenge: RateLimitChallenge) {
        withAccountOrThrow {
            val request = when (challenge) {
                is RateLimitChallenge.PushChallenge -> SubmitChallengeRequest(
                    account = accountId,
                    challenge = challenge.challengeToken,
                    captchaToken = null,
                )
                is RateLimitChallenge.Recaptcha -> SubmitChallengeRequest(
                    account = accountId,
                    challenge = challenge.challengeToken,
                    captchaToken = challenge.captchaToken,
                )
            }
            request.submitSuspend(socketWrapper)
        }
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
     * @throws InvalidRequestError
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     * @throws FingerprintVersionMismatchError
     * @throws InvalidBase64Error
     * @throws UnknownIdentityKeyError
     * @throws InvalidFingerprintError
     */
    public suspend fun trust(
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
            request.submitSuspend(socketWrapper)
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
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     * @throws InvalidRecipientError
     * @throws InvalidGroupError
     * @throws UntrustedIdentityError
     * @throws UnknownGroupError
     * @throws InvalidRequestError
     */
    public suspend fun typing(
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
            request.submitSuspend(socketWrapper)
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
     *  @throws NoSuchAccountError
     * @throws ServerNotFoundError
     * @throws InvalidProxyError
     * @throws InternalError
     */
    public suspend fun updateContact(
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
            ).submitSuspend(socketWrapper)
        }
    }

    /**
     * Update information about a group
     *
     * @param groupID The base64-encoded ID of the V2 group to update
     * @param groupUpdate The update to make to the group. This is done as a sealed class so that only one of the
     * modification actions can be performed at once. See [GroupUpdate] for details and all possible update types.
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     * @throws UnknownGroupError
     * @throws GroupVerificationError
     * @throws InvalidRequestError
     */
    public suspend fun updateGroup(groupID: String, groupUpdate: GroupUpdate): GroupInfo {
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
                is GroupUpdate.SetAnnouncement -> UpdateGroupRequest(
                    account = accountId,
                    groupID = groupID,
                    announcements = if (groupUpdate.setAnnouncementOnly) "ENABLED" else "DISABLED"
                )
            }
            return request.submitSuspend(socketWrapper)
        }
    }

    /**
     * Verify an account's phone number with a code after registering, completing the account creation
     * process.
     *
     * @param code The verification code from SMS or voice call.
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws AccountHasNoKeysError
     * @throws AccountAlreadyVerifiedError
     * @throws AccountLockedError
     * @throws NoSuchAccountError
     */
    public suspend fun verify(code: String) {
        val account = VerifyRequest(accountId, code).submitSuspend(socketWrapper)
        accountInfo = account
    }

    /**
     * Gets the version of signald
     *
     * @throws RequestFailedException if signald sends an error response or the incoming message is invalid
     * @throws SignaldException if the request to the socket fails
     */
    public suspend fun version(): JsonVersionMessage = VersionRequest().submitSuspend(socketWrapper)

    public companion object {
        public suspend fun create(accountId: String, socketPath: String? = null): Signal {
            return Signal(accountId = accountId, socketWrapper = NodeSocketWrapper.createSuspend(socketPath))
        }
    }
}
