package org.inthewaves.kotlinsignald.clientprotocol

import kotlinx.serialization.json.Json
import kotlin.native.concurrent.ThreadLocal

/**
 * The [Json] instance used to serialize and deserialize signald requests and responses.
 */
@ThreadLocal
public val SignaldJson: Json = kotlinx.serialization.json.Json { encodeDefaults = false }
