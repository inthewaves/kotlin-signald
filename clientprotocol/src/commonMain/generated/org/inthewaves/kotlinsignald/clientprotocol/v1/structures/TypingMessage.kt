// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TypingMessage(
    public val action: String? = null,
    public val timestamp: Long? = null,
    @SerialName("group_id")
    public val groupId: String? = null
)
