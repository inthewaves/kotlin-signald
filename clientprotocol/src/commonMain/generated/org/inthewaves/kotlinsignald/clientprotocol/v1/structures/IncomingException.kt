// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * An incoming message representing an exception that can be sent by signald corresponding to one of
 * the documented error types in the protocol.
 */
@Serializable
@SerialName("IncomingException")
public data class IncomingException(
    public override val version: String? = null,
    public override val data: Data,
    public override val error: Boolean? = false,
    public override val account: String? = null
) : ClientMessageWrapper() {
    public val typedException: TypedExceptionV1
        get() = data.typedException

    @Serializable
    public data class Data(
        /**
         * An exception given by Signald
         */
        public val typedException: TypedExceptionV1
    ) : ClientMessageWrapper.Data()
}
