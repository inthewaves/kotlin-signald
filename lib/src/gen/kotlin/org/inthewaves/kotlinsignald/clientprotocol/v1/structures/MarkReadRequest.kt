package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.MarkRead

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
@SerialName("mark_read")
public data class MarkReadRequest(
    /**
     * The account to interact with
     * Example: "+12024561414"
     */
    public val account: String,
    /**
     * The address that sent the message being marked as read
     */
    public val to: JsonAddress,
    /**
     * List of messages to mark as read
     * Example: 1615576442475
     */
    public val timestamps: List<Long>,
    public val `when`: Long? = null
) : SignaldRequestBodyV1<MarkRead, EmptyResponse>() {
    protected override val responseWrapperSerializer: KSerializer<MarkRead>
        get() = MarkRead.serializer()

    protected override val responseDataSerializer: KSerializer<EmptyResponse>
        get() = EmptyResponse.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        EmptyResponse? = if (responseWrapper is MarkRead && responseWrapper.data is
        EmptyResponse
    ) {
        responseWrapper.data
    } else {
        null
    }
}
