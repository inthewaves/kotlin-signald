// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Indicates the server rejected our credentials or a failed group update. Typically means the
 * linked device was removed by the primary device, or that the account was re-registered. For group
 * updates, this can indicate that we lack permissions.
 */
@Serializable
@SerialName("AuthorizationFailedError")
public data class AuthorizationFailedError(
    public override val message: String? = null
) : TypedExceptionV1()
