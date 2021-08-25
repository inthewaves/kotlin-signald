package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

/**
 * Wraps all incoming messages after a v1 subscribe request is issued
 *
 * Note that the `type` field has been removed. kotlinx.serialization uses that as a discriminator
 */
@Serializable
public sealed class ClientMessageWrapper {
    /**
     * the version of the object in the `data` field
     */
    public abstract val version: String?

    /**
     * the incoming object. The structure will vary from message to message, see `type` and
     * `version` fields
     */
    public abstract val data: Data

    /**
     * true if the incoming message represents an error
     */
    public abstract val error: Boolean?

    @Serializable
    public sealed class Data
}
