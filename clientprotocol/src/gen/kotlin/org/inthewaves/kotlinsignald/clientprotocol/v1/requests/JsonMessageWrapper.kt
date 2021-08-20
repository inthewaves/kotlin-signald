package org.inthewaves.kotlinsignald.clientprotocol.v1.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 *
 * Encapsulates the response schema from the signald socket.
 */
@Serializable
public sealed class JsonMessageWrapper<out Response> {
    public open val id: String? = null

    public abstract val `data`: Response?

    public open val error: JsonObject? = null

    public open val exception: String? = null

    @SerialName("error_type")
    public open val errorType: String? = null

    public open val version: String? = null

    public final val isSuccessful: Boolean
        get() = this !is UnexpectedError && data != null && error == null && errorType == null &&
            exception == null
}
