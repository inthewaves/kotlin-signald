package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import org.inthewaves.kotlinsignald.SignaldJson
import org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.Unsubscribe
import java.io.IOException

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 *
 * See subscribe for more info
 */
@Serializable
@SerialName("unsubscribe")
public data class UnsubscribeRequest(
    /**
     * The account to unsubscribe from
     * Example: "+12024561414"
     */
    public val account: String
) : SignaldRequestBodyV1<Unsubscribe, SubscriptionResponse>() {
    protected override val responseWrapperSerializer: KSerializer<Unsubscribe>
        get() = Unsubscribe.serializer()

    protected override val responseDataSerializer: KSerializer<SubscriptionResponse>
        get() = SubscriptionResponse.serializer()

    public override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        SubscriptionResponse? = if (responseWrapper is Unsubscribe && responseWrapper.data is
        SubscriptionResponse
    ) {
        responseWrapper.data
    } else {
        null
    }

    public override fun submit(socketCommunicator: SocketCommunicator, id: String):
        SubscriptionResponse {
        try {
            return super.submit(socketCommunicator, id)
        } catch (originalException: RequestFailedException) {
            // Because of race conditions where an incoming message can be sent / broadcasted through
            // the socket before we receive the unsubscribe acknowledgement message,
            // we parse and store all incoming messages until we get the ack.
            if (originalException.cause !is SerializationException) {
                throw originalException
            }
            var rawJsonResponse = originalException.responseJsonString ?: throw originalException
            val pendingChatMessages = mutableListOf<ClientMessageWrapper>()

            for (i in 0 until 10) {
                val incomingMessage: ClientMessageWrapper = try {
                    SignaldJson.decodeFromString(ClientMessageWrapper.serializer(), rawJsonResponse)
                } catch (e: SerializationException) {
                    val nextResponse: JsonMessageWrapper<*> = try {
                        SignaldJson.decodeFromString(
                            JsonMessageWrapper.serializer(responseDataSerializer),
                            rawJsonResponse
                        )
                    } catch (secondException: SerializationException) {
                        throw RequestFailedException(
                            responseJsonString = rawJsonResponse,
                            extraMessage = "failed to get incoming messages during unsubscribe",
                            cause = secondException
                        )
                    }
                    if (nextResponse.id == id && getTypedResponseOrNull(nextResponse) != null) {
                        return SubscriptionResponse(pendingChatMessages)
                    }
                    throw RequestFailedException(
                        responseJsonString = rawJsonResponse,
                        extraMessage = "failed to get incoming messages during unsubscribe",
                        cause = e
                    )
                }
                pendingChatMessages.add(incomingMessage)
                rawJsonResponse = socketCommunicator.readLine()
                    ?: throw IOException("unable to read line from socket", originalException)
            }

            throw IOException(
                "too many messages; didn't see subscribe acknowledgement",
                originalException
            )
        }
    }
}
