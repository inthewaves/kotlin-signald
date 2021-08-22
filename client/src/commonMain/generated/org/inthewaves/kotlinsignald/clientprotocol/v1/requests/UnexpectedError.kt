package org.inthewaves.kotlinsignald.clientprotocol.v1.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
@SerialName("unexpected_error")
public data class UnexpectedError private constructor(
    public override val data: JsonObject? = null
) : JsonMessageWrapper<JsonObject>()
