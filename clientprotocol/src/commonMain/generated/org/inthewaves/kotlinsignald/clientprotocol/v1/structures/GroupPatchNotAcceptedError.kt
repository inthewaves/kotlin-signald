// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Indicates the server rejected our group update. This can be due to errors such as trying to add a
 * user that's already in the group.
 */
@Serializable
@SerialName("GroupPatchNotAcceptedError")
public data class GroupPatchNotAcceptedError(
    public override val message: String? = null
) : TypedExceptionV1()
