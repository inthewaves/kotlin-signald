@file:JvmName("SocketUtil")
package org.inthewaves.kotlinsignald

import kotlinx.serialization.SerializationException
import org.inthewaves.kotlinsignald.clientprotocol.SignaldException
import org.inthewaves.kotlinsignald.clientprotocol.SignaldJson
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonVersionMessage
import kotlin.jvm.JvmName

internal fun decodeVersionOrNull(versionLine: String?) = if (versionLine != null) {
    try {
        SignaldJson.decodeFromString(JsonMessageWrapper.serializer(JsonVersionMessage.serializer()), versionLine).data
    } catch (e: SerializationException) {
        null
    }
} else {
    null
}

public class SocketUnavailableException : SignaldException {
    public constructor() : super()

    public constructor(message: String?) : super(message)

    public constructor(message: String?, cause: Throwable?) : super(message, cause)

    public constructor(cause: Throwable?) : super(cause)
}

internal expect fun getEnvVariable(envVarName: String): String?

internal fun getDefaultSocketPaths(): Sequence<String> =
    sequenceOf(
        getEnvVariable("XDG_RUNTIME_DIR")?.let { runtimeDir -> "$runtimeDir/signald/signald.sock" },
        "/var/run/signald/signald.sock"
    ).filterNotNull()
