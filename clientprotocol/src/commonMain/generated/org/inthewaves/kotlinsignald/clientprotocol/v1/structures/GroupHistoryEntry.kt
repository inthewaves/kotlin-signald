// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

@Serializable
public data class GroupHistoryEntry(
    public val group: JsonGroupV2Info? = null,
    public val change: GroupChange? = null
)
