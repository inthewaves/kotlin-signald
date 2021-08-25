package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

/**
 * details about a MobileCoin payment
 */
@Serializable
public data class Payment(
    /**
     * base64 encoded payment receipt data. This is a protobuf value which can be decoded as the
     * Receipt object described in
     * https://github.com/mobilecoinfoundation/mobilecoin/blob/master/api/proto/external.proto
     */
    public val receipt: String? = null,
    /**
     * note attached to the payment
     */
    public val note: String? = null
)
