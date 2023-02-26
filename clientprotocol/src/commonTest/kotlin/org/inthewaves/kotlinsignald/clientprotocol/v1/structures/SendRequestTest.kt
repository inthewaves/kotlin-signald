package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException
import org.inthewaves.kotlinsignald.clientprotocol.SignaldJson
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.assertThrows
import kotlin.test.*

internal class SendRequestTest {
    @Test
    fun testSerialization() {
        val testParams = sequenceOf(
            Pair(
                SendResponse(
                    results = listOf(
                        JsonSendMessageResult(
                            address = JsonAddress(
                                number = "+11112223333",
                                uuid = "f752327a-f947-4bdc-a6b5-2e53b95e6e06"
                            ),
                            success = SendSuccess(
                                unidentified = true,
                                needsSync = true,
                                duration = 234
                            ),
                            networkFailure = false,
                            unregisteredFailure = false,
                        ),
                    ),
                    timestamp = 1753824439411
                ),
                """
                    {
                        "type": "send",
                        "id": "$TEST_ID",
                        "data": {
                            "results": [
                                {
                                    "address": {
                                        "number": "+11112223333",
                                        "uuid": "f752327a-f947-4bdc-a6b5-2e53b95e6e06"
                                    },
                                    "success": {
                                        "unidentified": true,
                                        "needsSync": true,
                                        "duration": 234
                                    },
                                    "networkFailure": false,
                                    "unregisteredFailure": false
                                }
                            ],
                            "timestamp": 1753824439411
                        }
                    }
                """.trimIndent()
            )
        )

        testParams.forEach { (expected: SendResponse, socketResponseJson: String) ->
            val testUsername = "+1"
            val testGroupId = "GROUPID"
            val testSocketCommunicator = object : SocketCommunicator {
                override fun submit(request: String): String {
                    val requestObject = SignaldJson.decodeFromString<JsonObject>(request)
                    val version = requestObject["version"]
                    assertNotNull(version)
                    assertIs<JsonPrimitive>(version)
                    assertEquals(
                        "v1", version.content,
                        "bad request version v1: got $version (request: $requestObject)"
                    )
                    assertTrue(
                        (requestObject["recipientAddress"] != null) xor (requestObject["recipientGroupId"] != null),
                        "exactly one required of: recipientAddress, recipientGroupId (2 found): $requestObject"
                    )
                    if (requestObject["recipientGroupId"] != null) {
                        assertEquals(testGroupId, requestObject["recipientGroupId"]!!.jsonPrimitive.content)
                    }
                    assertEquals(testUsername, requestObject["username"]!!.jsonPrimitive.content)
                    return socketResponseJson
                }
                override fun readLine(): String = error("unused")
                override fun close() {}
            }

            val resp = SendRequest(
                username = testUsername,
                recipientGroupId = testGroupId
            ).submit(testSocketCommunicator, TEST_ID)
            assertEquals(expected, resp)
        }
    }

    @Test
    fun testUnexpectedError() {
        val exception = assertThrows<RequestFailedException> {
            val requestBody = SendRequest("+1", recipientGroupId = "GROUPID")
            requestBody.submit(object : SocketCommunicator {
                override fun submit(request: String): String {
                    return """
                            {
                                "id":"${requestBody.id}",
                                "type":"unexpected_error",
                                "data":{
                                    "msg_number":0,
                                    "message":"Unexpected character ('t' (code 116)): was expecting double-quote to start field name\n at [Source: (String)\"{typ{\"; line: 1, column: 3]",
                                    "error":true
                                }
                            }
                    """.trimIndent()
                }
                override fun readLine(): String = error("unused")
                override fun close() {}
            })
        }
        assertNull(exception.cause)
    }

    @Test
    fun testTypedExceptionThrown() {
        assertThrows<RateLimitError> {
            val requestBody = SendRequest("+1", recipientGroupId = "GROUPID")
            requestBody.submit(object : SocketCommunicator {
                override fun submit(request: String): String {
                    return """
                        {
                            "type":"send",
                            "id":"${requestBody.id}",
                            "error":{"message":"RateLimitException: Rate limited exceeded [413]"},
                            "error_type":"RateLimitError"
                        }
                    """.trimIndent()
                }
                override fun readLine(): String = error("unused")
                override fun close() {}
            })
        }
    }

    @Test
    fun testAuthorizationFailedError() {
        val exception = assertThrows<RequestFailedException> {
            val requestBody = SendRequest("+1", recipientGroupId = "GROUPID")
            requestBody.submit(object : SocketCommunicator {
                override fun submit(request: String): String {
                    // Thrown if the user reregisters from another device and attempting to send from a previously
                    // linked device.
                    return """
                        {
                            "type":"send",
                            "id":"${requestBody.id}",
                            "error":{"message":"java.util.concurrent.ExecutionException: org.whispersystems.signalservice.api.push.exceptions.AuthorizationFailedException: [401] Authorization failed!"},
                            "error_type":"IOException"
                        }
                    """.trimIndent()
                }
                override fun readLine(): String = error("unused")
                override fun close() {}
            })
        }
        assertNull(exception.cause)
        assertEquals("IOException", exception.errorType)
        assertEquals(
            buildJsonObject {
                put(
                    "message",
                    "java.util.concurrent.ExecutionException: org.whispersystems.signalservice.api.push.exceptions" +
                        ".AuthorizationFailedException: [401] Authorization failed!"
                )
            },
            exception.errorBody
        )
    }

    @Test
    fun testWrongReturnType() {
        val exception = assertThrows<RequestFailedException> {
            val requestBody = SendRequest("+1", recipientGroupId = "GROUPID")
            requestBody.submit(object : SocketCommunicator {
                override fun submit(request: String): String {
                    return """
                        {
                            "type":"version",
                            "id":"${requestBody.id}",
                            "data":{
                                "name":"signald",
                                "version":"0.14.1+git2021-08-13r7dde35de.21",
                                "branch":"main",
                                "commit":"7dde35de06e85a17c8e85c6d134eb1e98bf281e9"
                            }
                        }
                    """.trimIndent()
                }
                override fun readLine(): String = error("unused")
                override fun close() {}
            })
        }
        assertNull(exception.cause)
    }

    companion object {
        private const val TEST_ID = "55333"
    }
}
