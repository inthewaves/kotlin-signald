// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class HangupMessage(
    public val id: Long? = null,
    public val type: String? = null,
    public val legacy: Boolean? = null,
    @SerialName("device_id")
    public val deviceId: Int? = null
)
