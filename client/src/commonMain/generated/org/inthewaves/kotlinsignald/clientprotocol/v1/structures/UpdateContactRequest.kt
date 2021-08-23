package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.UpdateContact

/**
 * update information about a local contact
 */
@Serializable
@SerialName("update_contact")
public data class UpdateContactRequest(
    public val account: String,
    public val address: JsonAddress,
    public val name: String? = null,
    public val color: String? = null,
    @SerialName("inbox_position")
    public val inboxPosition: Int? = null
) : SignaldRequestBodyV1<Profile>() {
    internal override val responseWrapperSerializer: KSerializer<UpdateContact>
        get() = UpdateContact.serializer()

    internal override val responseDataSerializer: KSerializer<Profile>
        get() = Profile.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>): Profile? =
        if (responseWrapper is UpdateContact && responseWrapper.data is Profile) {
            responseWrapper.data
        } else {
            null
        }
}
