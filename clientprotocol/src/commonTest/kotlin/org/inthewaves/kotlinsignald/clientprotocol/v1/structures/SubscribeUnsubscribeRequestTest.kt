package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException
import org.inthewaves.kotlinsignald.clientprotocol.SignaldJson
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class SubscribeUnsubscribeRequestTest {
    private fun createMockSocketCommunicatorAndExpectedMessages(
        expectedAccount: String,
        isSubscribe: Boolean
    ): Pair<SocketCommunicator, List<ClientMessageWrapper>> {
        val mockSocketCommunicator = object : SocketCommunicator {
            // Test that messages before the (un)subscribe ack are parsed and saved
            // in the subscribe response.
            val action = if (isSubscribe) "subscribe" else "unsubscribe"
            val messageQueue = mutableListOf(
                """
                    {
                        "type":"ListenerState",
                        "version":"v1",
                        "data":{"connected":true}
                    }
                """.trimIndent(),
                """
                    {
                        "type":"ExceptionWrapper",
                        "version":"v1",
                        "data":{
                            "message":"org.signal.libsignal.metadata.ProtocolDuplicateMessageException",
                            "unexpected":true
                        },
                        "error":true
                    }
                """.trimIndent(),
                """
                    {
                        "type":"WebSocketConnectionState",
                        "version":"v1",
                        "data": {
                            "state":"CONNECTED",
                            "socket":"UNIDENTIFIED"
                        },
                        "account":"+1234567890"
                    }
                """.trimIndent(),
                """
                    {
                        "type":"UntrustedIdentityError",
                        "version":"v1",
                        "data":{"identifier":"e00eda40-e2c2-459c-87f4-304ae01f9d0a"},
                        "error":true,
                        "account":"+123"
                    }
                """.trimIndent(),
                """
                    {
                        "type":"InternalError",
                        "version":"v1",
                        "data":{
                            "exceptions":[
                                "org.signal.libsignal.metadata.ProtocolDuplicateMessageException",
                                "org.whispersystems.libsignal.DuplicateMessageException"
                            ],
                            "message":"org.signal.libsignal.metadata.ProtocolDuplicateMessageException: org.whispersystems.libsignal.DuplicateMessageException: message with old counter 1 / 0"
                        },
                        "error":true,
                        "account":"+123"
                    }
                """.trimIndent(),
                """
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
                """.trimIndent(),
                """{"type":"$action","id":"$TEST_MSG_ID","data":{}}""",
                """
                    {
                        "type":"ExceptionWrapper",
                        "version":"v1",
                        "data":{
                            "message":"this message should not be in the result",
                            "unexpected":true
                        },
                        "error":true
                    }
                """.trimIndent(),
            )

            override fun submit(request: String): String {
                val jsonRequest = SignaldJson.decodeFromString<JsonObject>(request)
                val version = jsonRequest["version"]
                assertTrue(
                    version is JsonPrimitive && version.content == "v1",
                    "bad version (got $version when expecting v1)"
                )
                val account = jsonRequest["account"]
                assertTrue(account is JsonPrimitive && account.content == expectedAccount, "missing account")
                return messageQueue.removeFirst()
            }

            override fun readLine(): String? = messageQueue.removeFirstOrNull()

            override fun close() {}
        }

        return mockSocketCommunicator to listOf(
            ListenerState(
                version = "v1",
                data = ListenerState.Data(connected = true)
            ),
            ExceptionWrapper(
                version = "v1",
                data = ExceptionWrapper.Data(
                    message = "org.signal.libsignal.metadata.ProtocolDuplicateMessageException",
                    unexpected = true
                ),
                error = true
            ),
            WebSocketConnectionState(
                version = "v1",
                data = WebSocketConnectionState.Data(
                    state = "CONNECTED",
                    socket = "UNIDENTIFIED"
                ),
                account = "+1234567890"
            ),
            IncomingException(
                version = "v1",
                data = IncomingException.Data(UntrustedIdentityError(identifier = "e00eda40-e2c2-459c-87f4-304ae01f9d0a")),
                error = true,
                account = "+123"
            ),
            IncomingException(
                version = "v1",
                data = IncomingException.Data(
                    InternalError(
                        exceptions = listOf(
                            "org.signal.libsignal.metadata.ProtocolDuplicateMessageException",
                            "org.whispersystems.libsignal.DuplicateMessageException"
                        ),
                        message = "org.signal.libsignal.metadata.ProtocolDuplicateMessageException: org.whispersystems.libsignal.DuplicateMessageException: message with old counter 1 / 0"
                    )
                ),
                error = true,
                account = "+123"
            ),
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
            )
        )
    }

    private fun runRaceConditionTest(isSubscribe: Boolean) {
        val expectedAccount = "+1234567890"
        val (mockSocketCommunicator, expectedMessages) =
            createMockSocketCommunicatorAndExpectedMessages(expectedAccount, isSubscribe = isSubscribe)
        val request = if (isSubscribe) SubscribeRequest(expectedAccount) else UnsubscribeRequest(expectedAccount)
        val response = request.submit(mockSocketCommunicator, id = TEST_MSG_ID)
        assertEquals(expectedMessages, response.messages)
    }

    @Test
    fun testSubscribeRequestRaceCondition() {
        runRaceConditionTest(isSubscribe = true)
    }

    @Test
    fun testUnsubscribeRequestRaceCondition() {
        runRaceConditionTest(isSubscribe = false)
    }

    @Test
    fun testSubscribeUnexpectedError() {
        assertThrows<RequestFailedException> {
            SubscribeRequest("+1").submit(object : SocketCommunicator {
                val messageQueue = mutableListOf(
                    """{"type":"ListenerState","version":"v1","data":{"connected":true}}""",
                    """
                        {
                            "id":"",
                            "type":"unexpected_error",
                            "data":{
                                "msg_number":0,
                                "message":"Unexpected character ('t' (code 116)): was expecting double-quote to start field name\n at [Source: (String)\"{typ{\"; line: 1, column: 3]",
                                "error":true
                            }
                        }
                    """.trimIndent(),
                    """{"type":"subscribe","id":"$TEST_MSG_ID","data":{}}""",
                )

                override fun submit(request: String): String {
                    return messageQueue.removeFirst()
                }

                override fun readLine(): String? {
                    return messageQueue.removeFirstOrNull()
                }

                override fun close() {}
            })
        }
    }

    companion object {
        private const val TEST_MSG_ID = "555"
    }
}
