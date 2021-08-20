package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.Serializable

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
@Deprecated("Will be removed on Sat, 1 Jan 2022 09:01:01 GMT")
public data class JsonAttachment(
    public val contentType: String? = null,
    public val id: String? = null,
    public val size: Int? = null,
    public val storedFilename: String? = null,
    public val filename: String? = null,
    public val customFilename: String? = null,
    public val caption: String? = null,
    public val width: Int? = null,
    public val height: Int? = null,
    public val voiceNote: Boolean? = null,
    public val key: String? = null,
    public val digest: String? = null,
    public val blurhash: String? = null
)
