package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

@Serializable
public data class LinkedDevices(
    public val devices: List<DeviceInfo> = emptyList()
) : SignaldResponseBodyV1()
