package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 *
 * An incoming message representing an error that can be sent by signald after a v1 subscribe
 * request. This is not documented in the client protocol; however, as the signald socket can send it
 * anyway, we add this here for type safety purposes.
 *
 * Example:
 * {
 *     "type":"ExceptionWrapper",
 *     "version":"v1",
 *     "data":{"message":"org.signal.libsignal.metadata.ProtocolDuplicateMessageException"},
 *     "error":true
 * }
 */
@Serializable
@SerialName("ExceptionWrapper")
public data class ExceptionWrapper(
    public override val version: String? = null,
    public override val data: Data,
    public override val error: Boolean? = false
) : ClientMessageWrapper() {
    @Serializable
    public data class Data(
        public val message: String? = null,
        public val unexpected: Boolean? = null
    ) : ClientMessageWrapper.Data()
}
