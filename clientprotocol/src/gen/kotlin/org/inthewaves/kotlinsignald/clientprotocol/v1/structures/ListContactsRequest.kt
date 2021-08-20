package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.ListContacts

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
@SerialName("list_contacts")
public data class ListContactsRequest(
    public val account: String,
    /**
     * return results from local store immediately, refreshing from server afterward if needed. If
     * false (default), block until all pending profiles have been retrieved.
     */
    public val async: Boolean? = null
) : SignaldRequestBodyV1<ListContacts, ProfileList>() {
    protected override val responseWrapperSerializer: KSerializer<ListContacts>
        get() = ListContacts.serializer()

    protected override val responseDataSerializer: KSerializer<ProfileList>
        get() = ProfileList.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>): ProfileList? =
        if (responseWrapper is ListContacts && responseWrapper.data is ProfileList) {
            responseWrapper.data
        } else {
            null
        }
}
