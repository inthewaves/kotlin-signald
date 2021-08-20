package org.inthewaves.kotlinsignald.clientprotocol

import kotlinx.serialization.json.Json

/**
 * The [Json] instance used to serialize and deserialize signald requests and responses.
 */
public val SignaldJson: Json = kotlinx.serialization.json.Json { encodeDefaults = false }
