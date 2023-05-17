// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * returned in response to use v1 groups, which are no longer supported
 */
@Serializable
@SerialName("UnsupportedGroupError")
public data class UnsupportedGroupError(
    public override val message: String? = null
) : TypedExceptionV1()
