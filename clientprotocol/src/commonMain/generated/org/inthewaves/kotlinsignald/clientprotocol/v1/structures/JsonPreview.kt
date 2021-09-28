// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

/**
 * metadata about one of the links in a message
 */
@Serializable
public data class JsonPreview(
    public val url: String? = null,
    public val title: String? = null,
    public val description: String? = null,
    public val date: Long? = null,
    /**
     * an optional image file attached to the preview
     */
    public val attachment: JsonAttachment? = null
)
