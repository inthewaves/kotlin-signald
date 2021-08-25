package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException
import org.inthewaves.kotlinsignald.clientprotocol.SignaldJson
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.assertThrows
import org.inthewaves.kotlinsignald.clientprotocol.v0.structures.Success
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class SendRequestTest {
    @Test
    fun testSerialization() {
        getTestParams().forEach { (expected: SendResponse, socketResponseJson: String) ->
            val testSocketCommunicator = object : SocketCommunicator {
                override fun submit(request: String): String {
                    val requestObject = SignaldJson.decodeFromString<JsonObject>(request)
                    val version = requestObject["version"]
                    assertTrue(
                        version != null && version is JsonPrimitive && version.content == "v1",
                        "bad request version v1: got $version (request: $requestObject)"
                    )
                    assertTrue(
                        (requestObject["recipientAddress"] != null) xor (requestObject["recipientGroupId"] != null),
                        "exactly one required of: recipientAddress, recipientGroupId (2 found): $requestObject"
                    )
                    return socketResponseJson
                }

                override fun readLine(): String? {
                    error("unused")
                }
            }

            val resp = SendRequest("+1", recipientGroupId = "GROUPID").submit(testSocketCommunicator, TEST_ID)
            assertEquals(expected, resp)
        }
    }

    @Test
    fun testUnexpectedError() {
        assertThrows<RequestFailedException> {
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

                override fun readLine(): String? {
                    error("unused")
                }
            })
        }
    }

    @Test
    fun testWrongReturnType() {
        assertThrows<RequestFailedException> {
            SendRequest("+1", recipientGroupId = "GROUPID")
                .submit(object : SocketCommunicator {
                    override fun submit(request: String): String {
                        return """
                            {
                                "type":"version",
                                "data":{
                                    "name":"signald",
                                    "version":"0.14.1+git2021-08-13r7dde35de.21",
                                    "branch":"main",
                                    "commit":"7dde35de06e85a17c8e85c6d134eb1e98bf281e9"
                                }
                            }
                        """.trimIndent()
                    }

                    override fun readLine(): String? {
                        error("unused")
                    }
                })
        }
    }

    companion object {
        private const val TEST_ID = "55333"

        fun getTestParams() = sequenceOf(
            Pair(
                SendResponse(
                    results = listOf(
                        JsonSendMessageResult(
                            address = JsonAddress(
                                number = "+11112223333",
                                uuid = "f752327a-f947-4bdc-a6b5-2e53b95e6e06"
                            ),
                            success = Success(
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
    }
}
