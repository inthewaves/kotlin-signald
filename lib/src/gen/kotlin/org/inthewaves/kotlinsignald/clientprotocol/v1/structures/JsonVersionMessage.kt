package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
public data class JsonVersionMessage(
    /**
     * Example: "signald"
     */
    public val name: String? = null,
    /**
     * Example: "0.14.1+git2021-08-13r7dde35de.21"
     */
    public val version: String? = null,
    /**
     * Example: "main"
     */
    public val branch: String? = null,
    /**
     * Example: "7dde35de06e85a17c8e85c6d134eb1e98bf281e9"
     */
    public val commit: String? = null
) : SignaldResponseBodyV1()
