package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

@Serializable
public data class SendResponse(
    public val results: List<JsonSendMessageResult> = emptyList(),
    /**
     * Example: 1615576442475
     */
    public val timestamp: Long? = null
) : SignaldResponseBodyV1()
