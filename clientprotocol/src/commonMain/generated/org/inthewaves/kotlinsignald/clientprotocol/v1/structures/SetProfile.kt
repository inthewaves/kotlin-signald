package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.SetProfile

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
@SerialName("set_profile")
public data class SetProfile(
    /**
     * The phone number of the account to use
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * New profile name. Set to empty string for no profile name
     * Example: "signald user"
     */
    public val name: String,
    /**
     * Path to new profile avatar file. If unset or null, unset the profile avatar
     * Example: "/tmp/image.jpg"
     */
    public val avatarFile: String? = null,
    /**
     * an optional about string. If unset, null or an empty string will unset profile about field
     */
    public val about: String? = null,
    /**
     * an optional single emoji character. If unset, null or an empty string will unset profile
     * emoji
     */
    public val emoji: String? = null,
    /**
     * an optional *base64-encoded* MobileCoin address to set in the profile. Note that this is not
     * the traditional MobileCoin address encoding, which is custom. Clients are responsible for
     * converting between MobileCoin's custom base58 on the user-facing side and base64 encoding on the
     * signald side. If unset, null or an empty string, will empty the profile payment address
     */
    @SerialName("mobilecoin_address")
    public val mobilecoinAddress: String? = null
) : SignaldRequestBodyV1<SetProfile, EmptyResponse>() {
    protected override val responseWrapperSerializer:
        KSerializer<org.inthewaves.kotlinsignald.clientprotocol.v1.requests.SetProfile>
            get() = org.inthewaves.kotlinsignald.clientprotocol.v1.requests.SetProfile.serializer()

    protected override val responseDataSerializer: KSerializer<EmptyResponse>
        get() = EmptyResponse.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        EmptyResponse? = if (responseWrapper is
        org.inthewaves.kotlinsignald.clientprotocol.v1.requests.SetProfile &&
        responseWrapper.data is EmptyResponse
    ) {
        responseWrapper.data
    } else {
        null
    }
}
