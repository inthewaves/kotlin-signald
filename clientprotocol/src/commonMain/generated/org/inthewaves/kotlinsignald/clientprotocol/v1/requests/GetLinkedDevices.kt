package org.inthewaves.kotlinsignald.clientprotocol.v1.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.LinkedDevices

/**
 * This class only represents the response from signald for the request. Make a request by creating
 * an instance of [org.inthewaves.kotlinsignald.clientprotocol.v1.structures.GetLinkedDevicesRequest]
 * and then calling its `submit` function.
 */
@Serializable
@SerialName("get_linked_devices")
public data class GetLinkedDevices private constructor(
    public override val `data`: LinkedDevices? = null
) : JsonMessageWrapper<LinkedDevices>()
