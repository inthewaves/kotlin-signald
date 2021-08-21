package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class IdentityKey(
    /**
     * the first time this identity key was seen
     */
    public val added: Long? = null,
    /**
     * Example: "373453558586758076680580548714989751943247272727416091564451"
     */
    @SerialName("safety_number")
    public val safetyNumber: String? = null,
    /**
     * base64-encoded QR code data
     */
    @SerialName("qr_code_data")
    public val qrCodeData: String? = null,
    /**
     * One of TRUSTED_UNVERIFIED, TRUSTED_VERIFIED or UNTRUSTED
     */
    @SerialName("trust_level")
    public val trustLevel: String? = null
)
