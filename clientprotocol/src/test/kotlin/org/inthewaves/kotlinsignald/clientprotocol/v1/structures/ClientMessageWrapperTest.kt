package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import org.inthewaves.kotlinsignald.clientprotocol.SignaldJson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ClientMessageWrapperTest {
    @Test
    fun testPolymorphicSerialization() {
        val jsonString = """{"type":"ListenerState","version":"v1","data":{"connected":true}}"""
        val clientMessageWrapper = SignaldJson.decodeFromString<ClientMessageWrapper>(jsonString)
        assertEquals(ListenerState(version = "v1", data = ListenerState.Data(connected = true)), clientMessageWrapper)

        assertThrows<SerializationException> {
            SignaldJson.decodeFromString<ListenerState>(jsonString)
        }

        val (expectedIncomingMessage, incomingMessageAsJsonString) =
            IncomingMessage(
                version = "v1",
                data = IncomingMessage.Data(
                    account = "+1234567890",
                    source = JsonAddress(
                        number = "+1337133713",
                        uuid = "d769c83e-8781-4161-97a6-22a58ae67e44"
                    ),
                    type = "RECEIPT",
                    timestamp = 12345,
                    sourceDevice = 5,
                    serverReceiverTimestamp = 5555,
                    serverDeliverTimestamp = 1111,
                    hasLegacyMessage = false,
                    hasContent = false,
                    unidentifiedSender = false,
                    serverGuid = "dd821c2c-708c-4f2b-9765-8bc32f4cc3fb",
                )
            ) to """
                {
                    "type": "IncomingMessage",
                    "version": "v1",
                    "data": {
                        "account": "+1234567890",
                        "source": {
                            "number": "+1337133713",
                            "uuid": "d769c83e-8781-4161-97a6-22a58ae67e44"
                        },
                        "type": "RECEIPT",
                        "timestamp": 12345,
                        "source_device": 5,
                        "server_receiver_timestamp": 5555,
                        "server_deliver_timestamp": 1111,
                        "has_legacy_message": false,
                        "has_content": false,
                        "unidentified_sender": false,
                        "server_guid": "dd821c2c-708c-4f2b-9765-8bc32f4cc3fb"
                    }
                }
            """.trimIndent()
        assertEquals(
            expectedIncomingMessage, SignaldJson.decodeFromString<ClientMessageWrapper>(incomingMessageAsJsonString)
        )

        val (expectedExceptionMessage, incomingExceptionAsJsonString) = ExceptionWrapper(
            version = "v1",
            data = ExceptionWrapper.Data(
                message = "org.signal.libsignal.metadata.ProtocolDuplicateMessageException"
            ),
            error = true
        ) to """
            {
                "type":"ExceptionWrapper",
                "version":"v1",
                "data":{
                    "message":"org.signal.libsignal.metadata.ProtocolDuplicateMessageException"
                },
                "error":true
            }
        """.trimIndent()
        assertEquals(
            expectedExceptionMessage, SignaldJson.decodeFromString<ClientMessageWrapper>(incomingExceptionAsJsonString)
        )

        val (anotherExceptionMessage, anotherExceptionAsJsonString) = ExceptionWrapper(
            version = "v1",
            data = ExceptionWrapper.Data(
                message = "org.signal.libsignal.metadata.ProtocolDuplicateMessageException",
                unexpected = true
            ),
            error = true
        ) to """
            {
                "type":"ExceptionWrapper",
                "version":"v1",
                "data":{
                    "message":"org.signal.libsignal.metadata.ProtocolDuplicateMessageException",
                    "unexpected":true
                },
                "error":true
            }
        """.trimIndent()
        assertEquals(
            anotherExceptionMessage, SignaldJson.decodeFromString<ClientMessageWrapper>(anotherExceptionAsJsonString)
        )
    }
}
