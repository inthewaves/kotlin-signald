package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.CreateGroup
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper

@Serializable
@SerialName("create_group")
public data class CreateGroupRequest(
    /**
     * The account to interact with
     *
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * Example: "Parkdale Run Club"
     */
    public val title: String,
    /**
     * Example: "/tmp/image.jpg"
     */
    public val avatar: String? = null,
    public val members: List<JsonAddress>,
    /**
     * the message expiration timer
     */
    public val timer: Int? = null,
    /**
     * The role of all members other than the group creator. Options are ADMINISTRATOR or DEFAULT
     * (case insensitive)
     *
     * Example: "ADMINISTRATOR"
     */
    @SerialName("member_role")
    public val memberRole: String? = null
) : SignaldRequestBodyV1<JsonGroupV2Info>() {
    internal override val responseWrapperSerializer: KSerializer<CreateGroup>
        get() = CreateGroup.serializer()

    internal override val responseDataSerializer: KSerializer<JsonGroupV2Info>
        get() = JsonGroupV2Info.serializer()

    internal override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        JsonGroupV2Info? = if (responseWrapper is CreateGroup && responseWrapper.data is
        JsonGroupV2Info
    ) {
        responseWrapper.data
    } else {
        null
    }
}
