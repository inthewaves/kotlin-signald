package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 *
 * indicates when the incoming connection to the signal server has started or stopped
 */
@Serializable
@SerialName("ListenerState")
public data class ListenerState(
    public override val version: String? = null,
    public override val data: Data,
    public override val error: Boolean? = false
) : ClientMessageWrapper() {
    @Serializable
    public data class Data(
        public val connected: Boolean? = null
    ) : ClientMessageWrapper.Data()
}
