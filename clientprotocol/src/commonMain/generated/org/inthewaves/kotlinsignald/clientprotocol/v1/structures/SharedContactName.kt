// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

@Serializable
public data class SharedContactName(
    /**
     * the full name that should be displayed
     */
    public val display: String? = null,
    /**
     * given name
     */
    public val given: String? = null,
    /**
     * middle name
     */
    public val middle: String? = null,
    /**
     * family name (surname)
     */
    public val family: String? = null,
    public val prefix: String? = null,
    public val suffix: String? = null
)
