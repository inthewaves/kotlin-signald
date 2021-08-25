package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.RequestSync

/**
 * Request other devices on the account send us their group list, syncable config and contact list.
 */
@Serializable
@SerialName("request_sync")
public data class RequestSyncRequest(
    /**
     * The account to use
     *
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * request group sync (default true)
     */
    public val groups: Boolean? = null,
    /**
     * request configuration sync (default true)
     */
    public val configuration: Boolean? = null,
    /**
     * request contact sync (default true)
     */
    public val contacts: Boolean? = null,
    /**
     * request block list sync (default true)
     */
    public val blocked: Boolean? = null
) : SignaldRequestBodyV1<EmptyResponse>() {
    internal override val responseWrapperSerializer: KSerializer<RequestSync>
        get() = RequestSync.serializer()

    internal override val responseDataSerializer: KSerializer<EmptyResponse>
        get() = EmptyResponse.serializer()

    internal override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        EmptyResponse? = if (responseWrapper is RequestSync && responseWrapper.data is
        EmptyResponse
    ) {
        responseWrapper.data
    } else {
        null
    }
}
