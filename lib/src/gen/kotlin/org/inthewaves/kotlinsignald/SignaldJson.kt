package org.inthewaves.kotlinsignald

import kotlinx.serialization.json.Json

internal val SignaldJson: Json = Json { encodeDefaults = false }
