// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.SuspendSocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.UpdateGroup

/**
 * modify a group. Note that only one modification action may be performed at once
 */
@Serializable
@SerialName("update_group")
public data class UpdateGroupRequest(
    /**
     * The identifier of the account to interact with
     *
     * Example: "0cc10e61-d64c-4dbc-b51c-334f7dd45a4a"
     */
    public val account: String,
    /**
     * the ID of the group to update
     *
     * Example: "EdSqI90cS0UomDpgUXOlCoObWvQOXlH5G3Z2d3f4ayE="
     */
    public val groupID: String,
    /**
     * Example: "Parkdale Run Club"
     */
    public val title: String? = null,
    /**
     * A new group description. Set to empty string to remove an existing description.
     *
     * Example: "A club for running in Parkdale"
     */
    public val description: String? = null,
    /**
     * Example: "/tmp/image.jpg"
     */
    public val avatar: String? = null,
    /**
     * update the group timer.
     */
    public val updateTimer: Int? = null,
    public val addMembers: List<JsonAddress>? = null,
    public val removeMembers: List<JsonAddress>? = null,
    public val updateRole: GroupMember? = null,
    /**
     * note that only one of the access controls may be updated per request
     */
    public val updateAccessControl: GroupAccessControl? = null,
    /**
     * regenerate the group link password, invalidating the old one
     */
    public val resetLink: Boolean? = null,
    /**
     * ENABLED to only allow admins to post messages, DISABLED to allow anyone to post
     */
    public val announcements: String? = null
) : SignaldRequestBodyV1<GroupInfo>() {
    internal override val responseWrapperSerializer: KSerializer<UpdateGroup>
        get() = UpdateGroup.serializer()

    internal override val responseDataSerializer: KSerializer<GroupInfo>
        get() = GroupInfo.serializer()

    internal override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>): GroupInfo? =
        if (responseWrapper is UpdateGroup && responseWrapper.data is GroupInfo) {
            responseWrapper.data
        } else {
            null
        }

    /**
     * @throws org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException if the signald
     * socket sends a bad or error response, or unable to serialize our request
     * @throws org.inthewaves.kotlinsignald.clientprotocol.SignaldException if an I/O error occurs
     * during socket communication
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     * @throws UnknownGroupError
     * @throws GroupVerificationError
     * @throws InvalidRequestError
     * @throws AuthorizationFailedError Can be caused if signald is setup as a linked device that
     * has been removed by the primary device. If trying to update a group, this can also be caused if
     * group permissions don't allow the update  (e.g. current role insufficient or not a member).
     * @throws UnregisteredUserError
     * @throws SQLError
     * @throws GroupPatchNotAcceptedError Caused when server rejects the group update, e.g. trying
     * to add a user that's already in the group
     * @throws UnsupportedGroupError
     */
    public override fun submit(socketCommunicator: SocketCommunicator, id: String): GroupInfo =
        super.submit(socketCommunicator, id)

    /**
     * @throws org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException if the signald
     * socket sends a bad or error response, or unable to serialize our request
     * @throws org.inthewaves.kotlinsignald.clientprotocol.SignaldException if an I/O error occurs
     * during socket communication
     * @throws InternalError
     * @throws InvalidProxyError
     * @throws ServerNotFoundError
     * @throws NoSuchAccountError
     * @throws UnknownGroupError
     * @throws GroupVerificationError
     * @throws InvalidRequestError
     * @throws AuthorizationFailedError Can be caused if signald is setup as a linked device that
     * has been removed by the primary device. If trying to update a group, this can also be caused if
     * group permissions don't allow the update  (e.g. current role insufficient or not a member).
     * @throws UnregisteredUserError
     * @throws SQLError
     * @throws GroupPatchNotAcceptedError Caused when server rejects the group update, e.g. trying
     * to add a user that's already in the group
     * @throws UnsupportedGroupError
     */
    public override suspend fun submitSuspend(
        socketCommunicator: SuspendSocketCommunicator,
        id: String
    ): GroupInfo = super.submitSuspend(socketCommunicator, id)
}
