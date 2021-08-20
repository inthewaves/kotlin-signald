package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 */
@Serializable
public data class CallMessage(
    @SerialName("offer_message")
    public val offerMessage: OfferMessage? = null,
    @SerialName("answer_message")
    public val answerMessage: AnswerMessage? = null,
    @SerialName("busy_message")
    public val busyMessage: BusyMessage? = null,
    @SerialName("hangup_message")
    public val hangupMessage: HangupMessage? = null,
    @SerialName("ice_update_message")
    public val iceUpdateMessage: List<IceUpdateMessage> = emptyList(),
    @SerialName("destination_device_id")
    public val destinationDeviceId: Int? = null,
    @SerialName("multi_ring")
    public val multiRing: Boolean? = null
)
