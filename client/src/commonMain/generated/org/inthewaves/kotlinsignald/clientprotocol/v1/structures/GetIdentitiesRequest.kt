package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.GetIdentities
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper

/**
 * Get information about a known keys for a particular address
 */
@Serializable
@SerialName("get_identities")
public data class GetIdentitiesRequest(
    /**
     * The account to interact with
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * address to get keys for
     */
    public val address: JsonAddress
) : SignaldRequestBodyV1<IdentityKeyList>() {
    internal override val responseWrapperSerializer: KSerializer<GetIdentities>
        get() = GetIdentities.serializer()

    internal override val responseDataSerializer: KSerializer<IdentityKeyList>
        get() = IdentityKeyList.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        IdentityKeyList? = if (responseWrapper is GetIdentities && responseWrapper.data is
        IdentityKeyList
    ) {
        responseWrapper.data
    } else {
        null
    }
}
