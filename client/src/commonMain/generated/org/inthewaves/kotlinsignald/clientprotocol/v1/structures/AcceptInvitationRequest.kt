package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.AcceptInvitation
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper

/**
 * Accept a v2 group invitation. Note that you must have a profile name set to join groups.
 */
@Serializable
@SerialName("accept_invitation")
public data class AcceptInvitationRequest(
    /**
     * The account to interact with
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * Example: "EdSqI90cS0UomDpgUXOlCoObWvQOXlH5G3Z2d3f4ayE="
     */
    public val groupID: String
) : SignaldRequestBodyV1<JsonGroupV2Info>() {
    internal override val responseWrapperSerializer: KSerializer<AcceptInvitation>
        get() = AcceptInvitation.serializer()

    internal override val responseDataSerializer: KSerializer<JsonGroupV2Info>
        get() = JsonGroupV2Info.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        JsonGroupV2Info? = if (responseWrapper is AcceptInvitation && responseWrapper.data is
        JsonGroupV2Info
    ) {
        responseWrapper.data
    } else {
        null
    }
}
