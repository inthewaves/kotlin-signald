package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.GetProfile
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper

/**
 * Get all information available about a user
 */
@Serializable
@SerialName("get_profile")
public data class GetProfileRequest(
    /**
     * the signald account to use
     */
    public val account: String,
    /**
     * if true, return results from local store immediately, refreshing from server in the
     * background if needed. if false (default), block until profile can be retrieved from server
     */
    public val async: Boolean? = null,
    /**
     * the address to look up
     */
    public val address: JsonAddress
) : SignaldRequestBodyV1<GetProfile, Profile>() {
    protected override val responseWrapperSerializer: KSerializer<GetProfile>
        get() = GetProfile.serializer()

    protected override val responseDataSerializer: KSerializer<Profile>
        get() = Profile.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>): Profile? =
        if (responseWrapper is GetProfile && responseWrapper.data is Profile) {
            responseWrapper.data
        } else {
            null
        }
}
