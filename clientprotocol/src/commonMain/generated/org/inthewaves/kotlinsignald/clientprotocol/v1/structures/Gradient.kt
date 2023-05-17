// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Gradient(
    public val colors: List<String> = emptyList(),
    public val angle: Int? = null,
    public val positions: List<Float> = emptyList(),
    /**
     * removed from Signal protocol
     */
    @SerialName("start_color")
    public val startColor: String? = null,
    /**
     * removed from Signal protocol
     */
    @SerialName("end_color")
    public val endColor: String? = null
)
