// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("CaptchaRequiredError")
public data class CaptchaRequiredError(
    public val more: String? = null,
    public val message: String? = null
) : TypedExceptionV1
