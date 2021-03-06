// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The result of fetching a group's history along with paging data.
 */
@Serializable
public data class GroupHistoryPage(
    public val results: List<GroupHistoryEntry> = emptyList(),
    @SerialName("paging_data")
    public val pagingData: PagingData? = null
) : SignaldResponseBodyV1()
