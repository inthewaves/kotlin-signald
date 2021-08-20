package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 *
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
