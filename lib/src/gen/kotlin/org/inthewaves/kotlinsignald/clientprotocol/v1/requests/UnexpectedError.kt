package org.inthewaves.kotlinsignald.clientprotocol.v1.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
@SerialName("unexpected_error")
public data class UnexpectedError private constructor(
    public override val data: JsonObject? = null
) : JsonMessageWrapper<JsonObject>()
