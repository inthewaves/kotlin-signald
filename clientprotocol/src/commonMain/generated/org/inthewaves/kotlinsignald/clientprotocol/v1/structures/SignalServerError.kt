// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * indicates signald received an http 500 status code from the server
 */
@Serializable
@SerialName("SignalServerError")
public data class SignalServerError(
    public override val message: String? = null
) : TypedExceptionV1()
