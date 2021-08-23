package org.inthewaves.kotlinsignald.clientprotocol.v0.structures

import kotlinx.serialization.Serializable

/**
 *
 *
 * https://github.com/signalapp/Signal-Android/blob/44d014c4459e9ac34b74800002fa86b402d0501c/libsignal/service/src/main/java/org/whispersystems/signalservice/api/messages/multidevice/StickerPackOperationMessage.java
 */
@Serializable
@Deprecated("Will be removed on Sat, 1 Jan 2022 09:01:01 GMT")
public data class JsonStickerPackOperationMessage(
    public val packID: String? = null,
    public val packKey: String? = null,
    public val type: String? = null
)
