package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.GetAllIdentities
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 *
 * get all known identity keys
 */
@Serializable
@SerialName("get_all_identities")
public data class GetAllIdentities(
    /**
     * The account to interact with
     * Example: "+12024561414"
     */
    public val account: String
) : SignaldRequestBodyV1<GetAllIdentities, AllIdentityKeyList>() {
    protected override val responseWrapperSerializer:
        KSerializer<org.inthewaves.kotlinsignald.clientprotocol.v1.requests.GetAllIdentities>
            get() =
                org.inthewaves.kotlinsignald.clientprotocol.v1.requests.GetAllIdentities.serializer()

    protected override val responseDataSerializer: KSerializer<AllIdentityKeyList>
        get() = AllIdentityKeyList.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        AllIdentityKeyList? = if (responseWrapper is
        org.inthewaves.kotlinsignald.clientprotocol.v1.requests.GetAllIdentities &&
        responseWrapper.data is AllIdentityKeyList
    ) {
        responseWrapper.data
    } else {
        null
    }
}
