package org.inthewaves.kotlinsignald

import org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException
import org.inthewaves.kotlinsignald.clientprotocol.SignaldException
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.AcceptInvitationRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.Account
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.AddLinkedDeviceRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.AddServerRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.AllIdentityKeyList
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ApproveMembershipRequest
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
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GroupLinkInfoRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.IdentityKeyList
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JoinGroupRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonAddress
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonGroupJoinInfo
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonGroupV2Info
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.LinkedDevices
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.LinkingURI
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ListAccountsRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.Profile
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.RegisterRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.RemoveServerRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.Server
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ServerList
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.SetProfile
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.VerifyRequest

/**
 * A signald client.
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
    private val socketWrapper = SocketWrapper(socketPath)

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
     * Whether the [accountId] of this [Signal] instance is registered with signald.
     *
     * Note that this does not detect registration with the Signal service itself. See
     * [https://gitlab.com/signald/signald/-/issues/192] for an issue about checking login status with the Signal
     * service
     */
    public val isRegisteredWithSignald: Boolean
        get() = accountInfo != null

    private fun getAccountOrNull() =
        ListAccountsRequest().submit(socketWrapper).accounts.find { it.accountId == accountId }

    private inline fun <T> withAccountOrThrow(block: (account: Account) -> T): T {
        val account = accountInfo ?: throw SignaldException("$accountId is not registered with signald")
        return block(account)
    }

    /**
     * Accept a group V2 invitation. Note that you must have a profile name set to join groups.
     * @param groupID A base-64 encoded ID of the group.
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
     * @param uri the `tsdevice:/` uri provided (typically in qr code form) by the new device. Example:
     * `tsdevice:/?uuid=jAaZ5lxLfh7zVw5WELd6-Q&pub_key=BfFbjSwmAgpVJBXUdfmSgf61eX3a%2Bq9AoxAVpl1HUap9`
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
     */
    @Throws(SignaldException::class)
    public fun addServer(server: Server): String {
        return AddServerRequest(server).submit(socketWrapper)
    }

    /**
     * Approves the [members] requests to join a V2 group with the given [groupID]. Returns the new group information.
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
     */
    @Throws(SignaldException::class)
    public fun generateLinkingUri(serverUuid: String? = null): LinkingURI {
        withAccountOrThrow {
            return GenerateLinkingURIRequest(server = serverUuid).submit(socketWrapper)
        }
    }

    /**
     * Returns all known identity keys for the current account
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
     */
    @Throws(SignaldException::class)
    public fun getGroup(groupID: String, revision: Int? = null): JsonGroupV2Info {
        withAccountOrThrow {
            return GetGroupRequest(account = accountId, groupID = groupID, revision = revision).submit(socketWrapper)
        }
    }

    /**
     * Get information about known identity keys for a particular [address].
     */
    @Throws(SignaldException::class)
    public fun getIdentities(address: JsonAddress): IdentityKeyList {
        withAccountOrThrow {
            return GetIdentitiesRequest(account = accountId, address = address).submit(socketWrapper)
        }
    }

    /**
     * Gets a list of all linked devices for this account.
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
     */
    @Throws(SignaldException::class)
    public fun getServers(): ServerList = GetServersRequest().submit(socketWrapper)

    /**
     * Get information about a group from a `signal.group` [groupLink].
     */
    @Throws(SignaldException::class)
    public fun getGroupLinkInfo(groupLink: String): JsonGroupJoinInfo {
        withAccountOrThrow {
            return GroupLinkInfoRequest(account = accountId, uri = groupLink).submit(socketWrapper)
        }
    }

    /**
     * Joins a group using the given `signal.group` [groupLink].
     */
    @Throws(SignaldException::class)
    public fun joinGroup(groupLink: String): JsonGroupJoinInfo {
        withAccountOrThrow {
            return JoinGroupRequest(account = accountId, uri = groupLink).submit(socketWrapper)
        }
    }

    @Throws(SignaldException::class)
    public fun register(voice: Boolean = false, captcha: String? = null) {
        RegisterRequest(
            account = accountId,
            voice = voice,
            captcha = captcha
        ).submit(socketWrapper)
    }

    @Throws(SignaldException::class)
    public fun verify(code: String) {
        val account = VerifyRequest(accountId, code).submit(socketWrapper)
        accountInfo = account
    }

    /**
     * Sets the profile of the current account. Note that all the parameters here will be used as the new profile
     * fields; leaving one of the fields unset means it will be treated as clearing it.
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
}
