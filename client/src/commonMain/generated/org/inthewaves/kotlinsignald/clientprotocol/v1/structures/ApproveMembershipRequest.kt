package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.ApproveMembership
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper

/**
 * approve a request to join a group
 */
@Serializable
@SerialName("approve_membership")
public data class ApproveMembershipRequest(
    /**
     * The account to interact with
     *
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * Example: "EdSqI90cS0UomDpgUXOlCoObWvQOXlH5G3Z2d3f4ayE="
     */
    public val groupID: String,
    /**
     * list of requesting members to approve
     */
    public val members: List<JsonAddress>
) : SignaldRequestBodyV1<JsonGroupV2Info>() {
    internal override val responseWrapperSerializer: KSerializer<ApproveMembership>
        get() = ApproveMembership.serializer()

    internal override val responseDataSerializer: KSerializer<JsonGroupV2Info>
        get() = JsonGroupV2Info.serializer()

    internal override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        JsonGroupV2Info? = if (responseWrapper is ApproveMembership && responseWrapper.data is
        JsonGroupV2Info
    ) {
        responseWrapper.data
    } else {
        null
    }
}
