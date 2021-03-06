// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SharedContactAvatar(
    public val attachment: JsonAttachment? = null,
    @SerialName("is_profile")
    public val isProfile: Boolean? = null
)
