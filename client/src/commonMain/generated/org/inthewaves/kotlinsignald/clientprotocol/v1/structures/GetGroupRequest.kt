package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.GetGroup
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper

/**
 * Query the server for the latest state of a known group. If no account in signald is a member of
 * the group (anymore), an error with error_type: 'UnknownGroupException' is returned.
 */
@Serializable
@SerialName("get_group")
public data class GetGroupRequest(
    /**
     * The account to interact with
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * Example: "EdSqI90cS0UomDpgUXOlCoObWvQOXlH5G3Z2d3f4ayE="
     */
    public val groupID: String,
    /**
     * the latest known revision, default value (-1) forces fetch from server
     */
    public val revision: Int? = null
) : SignaldRequestBodyV1<GetGroup, JsonGroupV2Info>() {
    protected override val responseWrapperSerializer: KSerializer<GetGroup>
        get() = GetGroup.serializer()

    protected override val responseDataSerializer: KSerializer<JsonGroupV2Info>
        get() = JsonGroupV2Info.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        JsonGroupV2Info? = if (responseWrapper is GetGroup && responseWrapper.data is
        JsonGroupV2Info
    ) {
        responseWrapper.data
    } else {
        null
    }
}