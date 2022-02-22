// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.SuspendSocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.GetGroupRevisionPages
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper

/**
 * Query the server for group revision history. The history contains information about the changes
 * between each revision and the user that made the change.
 */
@Serializable
@SerialName("get_group_revision_pages")
public data class GetGroupRevisionPagesRequest(
    /**
     * The account to interact with
     *
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * Example: "EdSqI90cS0UomDpgUXOlCoObWvQOXlH5G3Z2d3f4ayE="
     */
    @SerialName("group_id")
    public val groupId: String,
    /**
     * The revision to start the pages from. Note that if this is lower than the revision you joined
     * the group, an AuthorizationFailedError is returned.
     */
    @SerialName("from_revision")
    public val fromRevision: Int,
    /**
     * Whether to include the first state in the returned pages (default false)
     */
    @SerialName("include_first_revision")
    public val includeFirstRevision: Boolean? = null
) : SignaldRequestBodyV1<GroupHistoryPage>() {
    internal override val responseWrapperSerializer: KSerializer<GetGroupRevisionPages>
        get() = GetGroupRevisionPages.serializer()

    internal override val responseDataSerializer: KSerializer<GroupHistoryPage>
        get() = GroupHistoryPage.serializer()

    internal override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        GroupHistoryPage? = if (responseWrapper is GetGroupRevisionPages && responseWrapper.data
        is GroupHistoryPage
    ) {
        responseWrapper.data
    } else {
        null
    }

    /**
     * @throws org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException if the signald
     * socket sends a bad or error response, or unable to serialize our request
     * @throws org.inthewaves.kotlinsignald.clientprotocol.SignaldException if an I/O error occurs
     * during socket communication
     * @throws NoSuchAccountError
     * @throws UnknownGroupError
     * @throws ServerNotFoundError
     * @throws InvalidProxyError
     * @throws InternalError
     * @throws GroupVerificationError
     * @throws InvalidGroupStateError
     * @throws InvalidRequestError
     * @throws AuthorizationFailedError caused when not a member of the group, when requesting logs
     * from a revision lower than your joinedAtVersion, etc.
     * @throws RateLimitError
     */
    public override fun submit(socketCommunicator: SocketCommunicator, id: String): GroupHistoryPage =
        super.submit(socketCommunicator, id)

    /**
     * @throws org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException if the signald
     * socket sends a bad or error response, or unable to serialize our request
     * @throws org.inthewaves.kotlinsignald.clientprotocol.SignaldException if an I/O error occurs
     * during socket communication
     * @throws NoSuchAccountError
     * @throws UnknownGroupError
     * @throws ServerNotFoundError
     * @throws InvalidProxyError
     * @throws InternalError
     * @throws GroupVerificationError
     * @throws InvalidGroupStateError
     * @throws InvalidRequestError
     * @throws AuthorizationFailedError caused when not a member of the group, when requesting logs
     * from a revision lower than your joinedAtVersion, etc.
     * @throws RateLimitError
     */
    public override suspend fun submitSuspend(
        socketCommunicator: SuspendSocketCommunicator,
        id: String
    ): GroupHistoryPage = super.submitSuspend(socketCommunicator, id)
}