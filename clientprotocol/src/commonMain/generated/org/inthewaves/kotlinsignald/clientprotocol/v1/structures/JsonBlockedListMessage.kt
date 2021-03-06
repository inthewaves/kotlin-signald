// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

@Serializable
public data class JsonBlockedListMessage(
    public val addresses: List<JsonAddress> = emptyList(),
    public val groupIds: List<String> = emptyList()
)
