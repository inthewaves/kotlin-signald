// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

/**
 * represents a file attached to a message. When sending, only `filename` is required.
 */
@Serializable
public data class JsonAttachment(
    public val contentType: String? = null,
    public val id: String? = null,
    public val size: Int? = null,
    /**
     * when receiving, the path that file has been downloaded to
     */
    public val storedFilename: String? = null,
    /**
     * when sending, the path to the local file to upload
     */
    public val filename: String? = null,
    /**
     * the original name of the file
     */
    public val customFilename: String? = null,
    public val caption: String? = null,
    public val width: Int? = null,
    public val height: Int? = null,
    public val voiceNote: Boolean? = null,
    public val key: String? = null,
    public val digest: String? = null,
    public val blurhash: String? = null
)
