package org.inthewaves.kotlinsignald

import kotlinx.serialization.json.Json

/**
 * The [Json] instance used to serialize and deserialize signald requests and responses.
 */
public val SignaldJson: Json = Json { encodeDefaults = false }
