// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("InvalidBase64Error")
public data class InvalidBase64Error(
    public override val message: String? = null
) : TypedExceptionV1()
