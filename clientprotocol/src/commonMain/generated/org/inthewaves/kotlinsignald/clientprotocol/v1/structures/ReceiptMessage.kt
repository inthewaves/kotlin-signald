package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

@Serializable
public data class ReceiptMessage(
    /**
     * options: UNKNOWN, DELIVERY, READ, VIEWED
     */
    public val type: String? = null,
    public val timestamps: List<Long> = emptyList(),
    public val `when`: Long? = null
)
