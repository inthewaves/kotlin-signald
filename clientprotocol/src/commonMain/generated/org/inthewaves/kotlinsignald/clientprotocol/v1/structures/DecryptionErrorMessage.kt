// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DecryptionErrorMessage(
    public val timestamp: Long? = null,
    @SerialName("device_id")
    public val deviceId: Int? = null,
    @SerialName("ratchet_key")
    public val ratchetKey: String? = null
)
