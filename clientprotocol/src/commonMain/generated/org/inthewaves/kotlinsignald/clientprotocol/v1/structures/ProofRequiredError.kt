// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ProofRequiredError")
public data class ProofRequiredError(
    public val token: String? = null,
    /**
     * possible list values are RECAPTCHA and PUSH_CHALLENGE
     */
    public val options: List<String> = emptyList(),
    public override val message: String? = null,
    /**
     * value in seconds
     */
    @SerialName("retry_after")
    public val retryAfter: Long? = null
) : TypedExceptionV1()
