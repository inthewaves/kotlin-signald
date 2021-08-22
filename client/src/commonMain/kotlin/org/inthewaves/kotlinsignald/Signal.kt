package org.inthewaves.kotlinsignald

import org.inthewaves.kotlinsignald.clientprotocol.SignaldException
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.Account
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ListAccountsRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.RegisterRequest
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
            field = getAccountOrNull()
            return field
        }

    private fun getAccountOrNull() =
        ListAccountsRequest().submit(socketWrapper).accounts.find { it.accountId == accountId }

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

    @Throws(SignaldException::class)
    public fun setProfile(
        name: String,
        avatarFile: String?,
        about: String?,
        emoji: String?,
        mobilecoinAddress: String?
    ) {
        accountInfo ?: throw SignaldException("no account available")
        SetProfile(
            account = accountId,
            name = name,
            avatarFile = avatarFile,
            about = about,
            emoji = emoji,
            mobilecoinAddress = mobilecoinAddress
        ).submit(socketWrapper)
    }
}
