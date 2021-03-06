package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.decodeFromString
import org.inthewaves.kotlinsignald.clientprotocol.SignaldJson
import org.inthewaves.kotlinsignald.clientprotocol.v0.structures.ConfigurationMessage
import org.inthewaves.kotlinsignald.clientprotocol.v0.structures.JsonStickerPackOperationMessage
import org.inthewaves.kotlinsignald.clientprotocol.v0.structures.Optional
import kotlin.test.Test
import kotlin.test.assertEquals

internal class IncomingMessageTest {
    @Test
    fun testSerialization() {
        getTestParams().forEachIndexed { index, (expected: IncomingMessage, jsonString: String) ->
            val actual = SignaldJson.decodeFromString<ClientMessageWrapper>(jsonString)
            assertEquals(
                expected,
                actual,
                """
                    |Index [$index] failed
                    |Expected: $expected
                    |Actual  : $actual""".trimMargin()
            )
        }
    }

    @Test
    fun testSerializationWithExplicitSerializer() {
        getTestParams().forEachIndexed { index, (expected: IncomingMessage, jsonString: String) ->
            val actual = SignaldJson.decodeFromString(ClientMessageWrapper.serializer(), jsonString)
            assertEquals(
                expected,
                actual,
                """
                    |Index [$index] failed
                    |Expected: $expected
                    |Actual  : $actual""".trimMargin()
            )
        }
    }

    companion object {
        fun getTestParams() = sequenceOf(
            Pair(
                // Delivery receipt
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
                ),
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
                """.trimIndent()
            ),
            // Read receipt
            Pair(
                IncomingMessage(
                    version = "v1",
                    data = IncomingMessage.Data(
                        account = "+1234567890",
                        source = JsonAddress(
                            number = "+1337133713",
                            uuid = "d769c83e-8781-4161-97a6-22a58ae67e44"
                        ),
                        type = "UNIDENTIFIED_SENDER",
                        timestamp = 123457,
                        sourceDevice = 0,
                        serverReceiverTimestamp = 9000,
                        serverDeliverTimestamp = 90001,
                        hasLegacyMessage = false,
                        hasContent = true,
                        unidentifiedSender = true,
                        receiptMessage = ReceiptMessage(
                            type = "READ",
                            timestamps = listOf(11111111L),
                            `when` = 222222222L
                        ),
                        serverGuid = "dd821c2c-708c-4f2b-9765-8bc32f4cc3fb",
                    )
                ),
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
                            "type": "UNIDENTIFIED_SENDER",
                            "timestamp": 123457,
                            "source_device": 0,
                            "server_receiver_timestamp": 9000,
                            "server_deliver_timestamp": 90001,
                            "has_legacy_message": false,
                            "has_content": true,
                            "unidentified_sender": true,
                            "receipt_message": {
                                "type": "READ",
                                "timestamps": [11111111],
                                "when": 222222222
                            },
                            "server_guid": "dd821c2c-708c-4f2b-9765-8bc32f4cc3fb"
                        }
                    }
                """.trimIndent()
            ),
            // Prekey bundle from linked device
            Pair(
                IncomingMessage(
                    version = "v1",
                    data = IncomingMessage.Data(
                        account = "+1234567890",
                        source = JsonAddress(
                            number = "+1234567890",
                            uuid = "d769c83e-8781-4161-97a6-22a58ae67e44"
                        ),
                        type = "PREKEY_BUNDLE",
                        timestamp = 90004,
                        sourceDevice = 2,
                        serverReceiverTimestamp = 8000,
                        serverDeliverTimestamp = 80001,
                        hasLegacyMessage = false,
                        hasContent = true,
                        unidentifiedSender = true,
                        serverGuid = "dd821c2c-708c-4f2b-9765-8bc32f4cc3fb",
                        syncMessage = JsonSyncMessage(
                            contactsComplete = false,
                            request = "KEYS"
                        )
                    )
                ),
                """
                    {
                        "type": "IncomingMessage",
                        "version": "v1",
                        "data": {
                            "account": "+1234567890",
                            "source": {
                                "number": "+1234567890",
                                "uuid": "d769c83e-8781-4161-97a6-22a58ae67e44"
                            },
                            "type": "PREKEY_BUNDLE",
                            "timestamp": 90004,
                            "source_device": 2,
                            "server_receiver_timestamp": 8000,
                            "server_deliver_timestamp": 80001,
                            "has_legacy_message": false,
                            "has_content": true,
                            "unidentified_sender": true,
                            "sync_message": {
                                "contactsComplete": false,
                                "request": "KEYS"
                            },
                            "server_guid": "dd821c2c-708c-4f2b-9765-8bc32f4cc3fb"
                        }
                    }
                """.trimIndent()
            ),
            // Typing message
            Pair(
                IncomingMessage(
                    version = "v1",
                    data = IncomingMessage.Data(
                        account = "+1234567890",
                        source = JsonAddress(
                            number = "+1234567890",
                            uuid = "d769c83e-8781-4161-97a6-22a58ae67e44"
                        ),
                        type = "UNIDENTIFIED_SENDER",
                        timestamp = 90004,
                        sourceDevice = 0,
                        serverReceiverTimestamp = 8000,
                        serverDeliverTimestamp = 80001,
                        hasLegacyMessage = false,
                        hasContent = true,
                        unidentifiedSender = true,
                        typingMessage = TypingMessage(
                            action = "STARTED",
                            timestamp = 86543
                        ),
                        serverGuid = "dd821c2c-708c-4f2b-9765-8bc32f4cc3fb",
                    )
                ),
                """
                    {
                        "type": "IncomingMessage",
                        "version": "v1",
                        "data": {
                            "account": "+1234567890",
                            "source": {
                                "number": "+1234567890",
                                "uuid": "d769c83e-8781-4161-97a6-22a58ae67e44"
                            },
                            "type": "UNIDENTIFIED_SENDER",
                            "timestamp": 90004,
                            "source_device": 0,
                            "server_receiver_timestamp": 8000,
                            "server_deliver_timestamp": 80001,
                            "has_legacy_message": false,
                            "has_content": true,
                            "unidentified_sender": true,
                            "typing_message": {
                                "action": "STARTED",
                                "timestamp": 86543
                            },
                            "server_guid": "dd821c2c-708c-4f2b-9765-8bc32f4cc3fb"
                        }
                    }
                """.trimIndent()
            ),
            // Typing message ended, in a group
            Pair(
                IncomingMessage(
                    version = "v1",
                    data = IncomingMessage.Data(
                        account = "+1234567890",
                        source = JsonAddress(
                            number = "+1234567890",
                            uuid = "d769c83e-8781-4161-97a6-22a58ae67e44"
                        ),
                        type = "UNIDENTIFIED_SENDER",
                        timestamp = 90004,
                        sourceDevice = 0,
                        serverReceiverTimestamp = 8000,
                        serverDeliverTimestamp = 80001,
                        hasLegacyMessage = false,
                        hasContent = true,
                        unidentifiedSender = true,
                        typingMessage = TypingMessage(
                            action = "STOPPED",
                            timestamp = 1724340098565,
                            groupId = "Enfw3fE4fUm7RcfSUhEA1c7KAGmbZC2ot4oicB0ZXuk=",
                        ),
                        serverGuid = "dd821c2c-708c-4f2b-9765-8bc32f4cc3fb",
                    )
                ),
                """
                    {
                        "type": "IncomingMessage",
                        "version": "v1",
                        "data": {
                            "account": "+1234567890",
                            "source": {
                                "number": "+1234567890",
                                "uuid": "d769c83e-8781-4161-97a6-22a58ae67e44"
                            },
                            "type": "UNIDENTIFIED_SENDER",
                            "timestamp": 90004,
                            "source_device": 0,
                            "server_receiver_timestamp": 8000,
                            "server_deliver_timestamp": 80001,
                            "has_legacy_message": false,
                            "has_content": true,
                            "unidentified_sender": true,
                            "typing_message": {
                                "action": "STOPPED",
                                "timestamp": 1724340098565,
                                "group_id": "Enfw3fE4fUm7RcfSUhEA1c7KAGmbZC2ot4oicB0ZXuk="
                            },
                            "server_guid": "dd821c2c-708c-4f2b-9765-8bc32f4cc3fb"
                        }
                    }
                """.trimIndent()
            ),
            // Normal Signal text message
            Pair(
                IncomingMessage(
                    version = "v1",
                    data = IncomingMessage.Data(
                        account = "+123",
                        source = JsonAddress(
                            number = "+456",
                            uuid = "d769c83e-8781-4161-97a6-22a58ae67e44"
                        ),
                        type = "UNIDENTIFIED_SENDER",
                        timestamp = 70004,
                        sourceDevice = 0,
                        serverReceiverTimestamp = 8000,
                        serverDeliverTimestamp = 80001,
                        hasLegacyMessage = false,
                        hasContent = true,
                        unidentifiedSender = true,
                        dataMessage = JsonDataMessage(
                            timestamp = 1111111111111,
                            body = "T",
                            endSession = false,
                            expiresInSeconds = 0,
                            profileKeyUpdate = false,
                            viewOnce = false
                        ),
                        serverGuid = "d769c83e-8781-4161-97a6-22a58ae67e44",
                    )
                ),
                """
                    {
                        "type": "IncomingMessage",
                        "version": "v1",
                        "data": {
                            "account": "+123",
                            "source": {
                                "number": "+456",
                                "uuid": "d769c83e-8781-4161-97a6-22a58ae67e44"
                            },
                            "type": "UNIDENTIFIED_SENDER",
                            "timestamp": 70004,
                            "source_device": 0,
                            "server_receiver_timestamp": 8000,
                            "server_deliver_timestamp": 80001,
                            "has_legacy_message": false,
                            "has_content": true,
                            "unidentified_sender": true,
                            "data_message": {
                                "timestamp": 1111111111111,
                                "body": "T",
                                "endSession": false,
                                "expiresInSeconds": 0,
                                "profileKeyUpdate": false,
                                "viewOnce": false
                            },
                            "server_guid": "d769c83e-8781-4161-97a6-22a58ae67e44"
                        }
                    }
                """.trimIndent()
            ),
            // Signal text message with a shared contact
            Pair(
                IncomingMessage(
                    version = "v1",
                    data = IncomingMessage.Data(
                        account = "+123",
                        source = JsonAddress(
                            number = "+456",
                            uuid = "d769c83e-8781-4161-97a6-22a58ae67e44"
                        ),
                        type = "UNIDENTIFIED_SENDER",
                        timestamp = 70004,
                        sourceDevice = 0,
                        serverReceiverTimestamp = 8000,
                        serverDeliverTimestamp = 80001,
                        hasLegacyMessage = false,
                        hasContent = true,
                        unidentifiedSender = true,
                        dataMessage = JsonDataMessage(
                            timestamp = 1111111111111,
                            endSession = false,
                            expiresInSeconds = 0,
                            profileKeyUpdate = false,
                            contacts = listOf(
                                SharedContact(
                                    name = SharedContactName(
                                        display = "TestPrefix TestGivenName TestMiddleName TestLastName TestSuffix",
                                        given = "TestGivenName",
                                        middle = "TestMiddleName",
                                        family = "TestLastName",
                                        prefix = "TestPrefix",
                                        suffix = "TestSuffix",
                                    ),
                                    email = listOf(
                                        SharedContactEmail(
                                            type = "HOME",
                                            value = "example@example.com",
                                            label = ""
                                        ),
                                        // using the Other type inside the AOSP contacts app
                                        SharedContactEmail(
                                            type = "CUSTOM",
                                            value = "other@example.com",
                                            label = ""
                                        ),
                                        SharedContactEmail(
                                            type = "CUSTOM",
                                            value = "customlabel@example.com",
                                            label = "CustomLabel"
                                        ),
                                    ),
                                    phone = listOf(
                                        SharedContactPhone(
                                            type = "MOBILE",
                                            value = "+123",
                                            label = ""
                                        ),
                                        SharedContactPhone(
                                            type = "CUSTOM",
                                            value = "1337",
                                            label = "Custom phone"
                                        ),
                                        SharedContactPhone(
                                            type = "HOME",
                                            value = "123",
                                            label = "",
                                        ),
                                        SharedContactPhone(
                                            type = "CUSTOM",
                                            value = "224",
                                            label = "",
                                        )
                                    ),
                                    address = listOf(
                                        SharedContactAddress(
                                            type = "HOME",
                                            label = "",
                                            street = "1234 Test Street",
                                            pobox = "",
                                            neighborhood = "",
                                            city = "",
                                            region = "",
                                            postcode = "",
                                            country = ""
                                        )
                                    ),
                                    avatar = SharedContactAvatar(
                                        attachment = JsonAttachment(
                                            contentType = "image/jpeg",
                                            id = "4911111111111111100",
                                            size = 9226,
                                            width = 720,
                                            height = 720,
                                            voiceNote = false,
                                            key = "aFqkZ2aLj00RRbnUmkYt77AXRMNejPxTbYPd1utI2uLaD3bYebbcaXxbd/X9pnM/gJQ4vJFOdH7/ATEMQE/TJQ==",
                                            digest = "lH6wWCrMzC5TRZhcr5W5idoqvZ5PRR0FCSQ+90D8PfI="
                                        ),
                                        isProfile = false
                                    )
                                )
                            ),
                            viewOnce = false
                        ),
                        serverGuid = "d769c83e-8781-4161-97a6-22a58ae67e44",
                    )
                ),
                """
                    {
                        "type": "IncomingMessage",
                        "version": "v1",
                        "data": {
                            "account": "+123",
                            "source": {
                                "number": "+456",
                                "uuid": "d769c83e-8781-4161-97a6-22a58ae67e44"
                            },
                            "type": "UNIDENTIFIED_SENDER",
                            "timestamp": 70004,
                            "source_device": 0,
                            "server_receiver_timestamp": 8000,
                            "server_deliver_timestamp": 80001,
                            "has_legacy_message": false,
                            "has_content": true,
                            "unidentified_sender": true,
                            "data_message": {
                                "timestamp": 1111111111111,
                                "endSession": false,
                                "expiresInSeconds": 0,
                                "profileKeyUpdate": false,
                                "contacts": [
                                    {
                                        "name": {
                                            "display": "TestPrefix TestGivenName TestMiddleName TestLastName TestSuffix",
                                            "given": "TestGivenName",
                                            "middle": "TestMiddleName",
                                            "family": "TestLastName",
                                            "prefix": "TestPrefix",
                                            "suffix": "TestSuffix"
                                        },
                                        "email": [
                                            {
                                                "type": "HOME",
                                                "value": "example@example.com",
                                                "label": ""
                                            },
                                            {
                                                "type": "CUSTOM",
                                                "value": "other@example.com",
                                                "label": ""
                                            },
                                            {
                                                "type": "CUSTOM",
                                                "value": "customlabel@example.com",
                                                "label": "CustomLabel"
                                            }
                                        ],
                                        "phone": [
                                            {
                                                "type": "MOBILE",
                                                "value": "+123",
                                                "label": ""
                                            },
                                            {
                                                "type": "CUSTOM",
                                                "value": "1337",
                                                "label": "Custom phone"
                                            },
                                            {
                                                "type": "HOME",
                                                "value": "123",
                                                "label": ""
                                            },
                                            {
                                                "type": "CUSTOM",
                                                "value": "224",
                                                "label": ""
                                            }
                                        ],
                                        "address": [
                                            {
                                                "type": "HOME",
                                                "label": "",
                                                "street": "1234 Test Street",
                                                "pobox": "",
                                                "neighborhood": "",
                                                "city": "",
                                                "region": "",
                                                "postcode": "",
                                                "country": ""
                                            }
                                        ],
                                        "avatar": {
                                            "attachment": {
                                                "contentType": "image/jpeg",
                                                "id": "4911111111111111100",
                                                "size": 9226,
                                                "width": 720,
                                                "height": 720,
                                                "voiceNote": false,
                                                "key": "aFqkZ2aLj00RRbnUmkYt77AXRMNejPxTbYPd1utI2uLaD3bYebbcaXxbd/X9pnM/gJQ4vJFOdH7/ATEMQE/TJQ==",
                                                "digest": "lH6wWCrMzC5TRZhcr5W5idoqvZ5PRR0FCSQ+90D8PfI="
                                            },
                                            "is_profile": false
                                        }
                                    }
                                ],
                                "viewOnce": false
                            },
                            "server_guid": "d769c83e-8781-4161-97a6-22a58ae67e44"
                        }
                    }
                """.trimIndent()
            ),
            // Signal text message with a shared contact
            Pair(
                IncomingMessage(
                    version = "v1",
                    data = IncomingMessage.Data(
                        account = "+123",
                        source = JsonAddress(
                            number = "+456",
                            uuid = "d769c83e-8781-4161-97a6-22a58ae67e44"
                        ),
                        type = "UNIDENTIFIED_SENDER",
                        timestamp = 70004,
                        sourceDevice = 0,
                        serverReceiverTimestamp = 8000,
                        serverDeliverTimestamp = 80001,
                        hasLegacyMessage = false,
                        hasContent = true,
                        unidentifiedSender = true,
                        callMessage = CallMessage(
                            offerMessage = OfferMessage(
                                id = 111111111111111,
                                type = "audio_call",
                                opaque = "Some really big base64-encoded data",
                            ),
                            multiRing = true
                        ),
                        serverGuid = "d769c83e-8781-4161-97a6-22a58ae67e44",
                    )
                ),
                """
                    {
                        "type": "IncomingMessage",
                        "version": "v1",
                        "data": {
                            "account": "+123",
                            "source": {
                                "number": "+456",
                                "uuid": "d769c83e-8781-4161-97a6-22a58ae67e44"
                            },
                            "type": "UNIDENTIFIED_SENDER",
                            "timestamp": 70004,
                            "source_device": 0,
                            "server_receiver_timestamp": 8000,
                            "server_deliver_timestamp": 80001,
                            "has_legacy_message": false,
                            "has_content": true,
                            "unidentified_sender": true,
                            "call_message": {
                                "offer_message": {
                                    "id": 111111111111111,
                                    "type": "audio_call",
                                    "opaque": "Some really big base64-encoded data"
                                },
                                "multi_ring": true
                            },
                            "server_guid": "d769c83e-8781-4161-97a6-22a58ae67e44"
                        }
                    }
                """.trimIndent()
            ),
            // GroupV2 join message
            Pair(
                IncomingMessage(
                    version = "v1",
                    data = IncomingMessage.Data(
                        account = "+123",
                        source = JsonAddress(
                            number = "+456",
                            uuid = "d769c83e-8781-4161-97a6-22a58ae67e44"
                        ),
                        type = "UNIDENTIFIED_SENDER",
                        timestamp = 70004,
                        sourceDevice = 0,
                        serverReceiverTimestamp = 8000,
                        serverDeliverTimestamp = 80001,
                        hasLegacyMessage = false,
                        hasContent = true,
                        unidentifiedSender = true,
                        dataMessage = JsonDataMessage(
                            timestamp = 1111111111111,
                            groupV2 = JsonGroupV2Info(
                                id = "groupv2id as base64 string=",
                                revision = 55,
                                timer = 0
                            ),
                            endSession = false,
                            expiresInSeconds = 0,
                            profileKeyUpdate = false,
                            viewOnce = false
                        ),
                        serverGuid = "d769c83e-8781-4161-97a6-22a58ae67e44",
                    )
                ),
                """
                    {
                        "type": "IncomingMessage",
                        "version": "v1",
                        "data": {
                            "account": "+123",
                            "source": {
                                "number": "+456",
                                "uuid": "d769c83e-8781-4161-97a6-22a58ae67e44"
                            },
                            "type": "UNIDENTIFIED_SENDER",
                            "timestamp": 70004,
                            "source_device": 0,
                            "server_receiver_timestamp": 8000,
                            "server_deliver_timestamp": 80001,
                            "has_legacy_message": false,
                            "has_content": true,
                            "unidentified_sender": true,
                            "data_message": {
                                "timestamp": 1111111111111,
                                "groupV2": {
                                    "id": "groupv2id as base64 string=",
                                    "revision": 55,
                                    "timer": 0
                                },
                                "endSession": false,
                                "expiresInSeconds": 0,
                                "profileKeyUpdate": false,
                                "viewOnce": false
                            },
                            "server_guid": "d769c83e-8781-4161-97a6-22a58ae67e44"
                        }
                    }
                """.trimIndent()
            ),
            // Messages received after a sync after linking to an existing install: First one
            Pair(
                IncomingMessage(
                    version = "v1",
                    data = IncomingMessage.Data(
                        account = "+11111111111",
                        source = JsonAddress(
                            number = "+11111111111",
                            uuid = "2f70e07c-62ad-4f09-a288-a9285b545963"
                        ),
                        type = "CIPHERTEXT",
                        timestamp = 1111870121111,
                        sourceDevice = 1,
                        serverReceiverTimestamp = 1111990121111,
                        serverDeliverTimestamp = 1411990124444,
                        hasLegacyMessage = false,
                        hasContent = true,
                        unidentifiedSender = false,
                        syncMessage = JsonSyncMessage(
                            contactsComplete = false,
                            configuration = ConfigurationMessage(
                                readReceipts = Optional(present = true),
                                unidentifiedDeliveryIndicators = Optional(present = true),
                                typingIndicators = Optional(present = true),
                                linkPreviews = Optional(present = true),
                            ),
                        ),
                        serverGuid = "6532b52f-9e2c-41dd-92d5-08fad64faa9a"
                    )
                ),
                """
                    {
                        "type": "IncomingMessage",
                        "version": "v1",
                        "data": {
                            "account": "+11111111111",
                            "source": {
                                "number": "+11111111111",
                                "uuid": "2f70e07c-62ad-4f09-a288-a9285b545963"
                            },
                            "type": "CIPHERTEXT",
                            "timestamp": 1111870121111,
                            "source_device": 1,
                            "server_receiver_timestamp": 1111990121111,
                            "server_deliver_timestamp": 1411990124444,
                            "has_legacy_message": false,
                            "has_content": true,
                            "unidentified_sender": false,
                            "sync_message": {
                                "contactsComplete": false,
                                "configuration": {
                                    "readReceipts": {
                                        "present": true
                                    },
                                    "unidentifiedDeliveryIndicators": {
                                        "present": true
                                    },
                                    "typingIndicators": {
                                        "present": true
                                    },
                                    "linkPreviews": {
                                        "present": true
                                    }
                                }
                            },
                            "server_guid": "6532b52f-9e2c-41dd-92d5-08fad64faa9a"
                        }
                    }
                """.trimIndent()
            ),
            // Messages received after a sync after linking to an existing install: Contact sync download
            Pair(
                IncomingMessage(
                    version = "v1",
                    data = IncomingMessage.Data(
                        account = "+11111111111",
                        source = JsonAddress(
                            number = "+11111111111",
                            uuid = "2f70e07c-62ad-4f09-a288-a9285b545963"
                        ),
                        type = "CIPHERTEXT",
                        timestamp = 1111870121111,
                        sourceDevice = 1,
                        serverReceiverTimestamp = 1111990121111,
                        serverDeliverTimestamp = 1411990124444,
                        hasLegacyMessage = false,
                        hasContent = true,
                        unidentifiedSender = false,
                        syncMessage = JsonSyncMessage(
                            contacts = JsonAttachment(
                                contentType = "application/octet-stream",
                                id = "abcD1f23hijKSmIdhaq5",
                                size = 15324,
                                width = 0,
                                height = 0,
                                voiceNote = false,
                                key = "yFqkP2bLj00RWbnUmkYt77AXRMNejPxTbYPD1utI2uLaD3bYebbcaXxbd/X9pnM/gJQ4vJFOdH7/AT" +
                                    "EMQE/TJQ==",
                                digest = "lH6oWCrMzC5TRZhcr5W5idoqvZ5PRR0FCSQ+90D8PfI=",
                            ),
                            contactsComplete = true,
                        ),
                        serverGuid = "6532b52f-9e2c-41dd-92d5-08fad64faa9a"
                    )
                ),
                """
                    {
                        "type": "IncomingMessage",
                        "version": "v1",
                        "data": {
                            "account": "+11111111111",
                            "source": {
                                "number": "+11111111111",
                                "uuid": "2f70e07c-62ad-4f09-a288-a9285b545963"
                            },
                            "type": "CIPHERTEXT",
                            "timestamp": 1111870121111,
                            "source_device": 1,
                            "server_receiver_timestamp": 1111990121111,
                            "server_deliver_timestamp": 1411990124444,
                            "has_legacy_message": false,
                            "has_content": true,
                            "unidentified_sender": false,
                            "sync_message": {
                                "contacts": {
                                    "contentType": "application/octet-stream",
                                    "id": "abcD1f23hijKSmIdhaq5",
                                    "size": 15324,
                                    "width": 0,
                                    "height": 0,
                                    "voiceNote": false,
                                    "key": "yFqkP2bLj00RWbnUmkYt77AXRMNejPxTbYPD1utI2uLaD3bYebbcaXxbd/X9pnM/gJQ4vJFOdH7/ATEMQE/TJQ==",
                                    "digest": "lH6oWCrMzC5TRZhcr5W5idoqvZ5PRR0FCSQ+90D8PfI="
                                },
                                "contactsComplete": true
                            },
                            "server_guid": "6532b52f-9e2c-41dd-92d5-08fad64faa9a"
                        }
                    }
                """.trimIndent()
            ),
            // Messages received after a sync after linking to an existing install: Sticker pack install
            Pair(
                IncomingMessage(
                    version = "v1",
                    data = IncomingMessage.Data(
                        account = "+11111111111",
                        source = JsonAddress(
                            number = "+11111111111",
                            uuid = "2f70e07c-62ad-4f09-a288-a9285b545963"
                        ),
                        type = "CIPHERTEXT",
                        timestamp = 1111870121111,
                        sourceDevice = 1,
                        serverReceiverTimestamp = 1111990121111,
                        serverDeliverTimestamp = 1411990124444,
                        hasLegacyMessage = false,
                        hasContent = true,
                        unidentifiedSender = false,
                        syncMessage = JsonSyncMessage(
                            contactsComplete = false,
                            stickerPackOperations = listOf(
                                JsonStickerPackOperationMessage(
                                    packID = "a4e90a541102c26c243b96da6fb286b1",
                                    packKey = "dce0b064dd2c096d631493f36721680391a31f287f58c2301cd671b1b1c98144",
                                    type = "INSTALL"
                                ),
                                JsonStickerPackOperationMessage(
                                    packID = "d7133e5b6fcb7b4d70430876968700ee",
                                    packKey = "40470a73603162a791d1928bdb666271897f92f8874f67106a89eadcd1576a13",
                                    type = "INSTALL"
                                ),
                                JsonStickerPackOperationMessage(
                                    packID = "9b0b080db489d005be67548235605361",
                                    packKey = "f0ee84b8ca7409a21421a0f753587778c3bf75430cc09d8bb7ebbc3bf8c1eeac",
                                    type = "INSTALL"
                                ),
                            )
                        ),
                        serverGuid = "6532b52f-9e2c-41dd-92d5-08fad64faa9a"
                    )
                ),
                """
                    {
                        "type": "IncomingMessage",
                        "version": "v1",
                        "data": {
                            "account": "+11111111111",
                            "source": {
                                "number": "+11111111111",
                                "uuid": "2f70e07c-62ad-4f09-a288-a9285b545963"
                            },
                            "type": "CIPHERTEXT",
                            "timestamp": 1111870121111,
                            "source_device": 1,
                            "server_receiver_timestamp": 1111990121111,
                            "server_deliver_timestamp": 1411990124444,
                            "has_legacy_message": false,
                            "has_content": true,
                            "unidentified_sender": false,
                            "sync_message": {
                                "contactsComplete": false,
                                "stickerPackOperations": [
                                    {
                                        "packID": "a4e90a541102c26c243b96da6fb286b1",
                                        "packKey": "dce0b064dd2c096d631493f36721680391a31f287f58c2301cd671b1b1c98144",
                                        "type": "INSTALL"
                                    },
                                    {
                                        "packID": "d7133e5b6fcb7b4d70430876968700ee",
                                        "packKey": "40470a73603162a791d1928bdb666271897f92f8874f67106a89eadcd1576a13",
                                        "type": "INSTALL"
                                    },
                                    {
                                        "packID": "9b0b080db489d005be67548235605361",
                                        "packKey": "f0ee84b8ca7409a21421a0f753587778c3bf75430cc09d8bb7ebbc3bf8c1eeac",
                                        "type": "INSTALL"
                                    }
                                ]
                            },
                            "server_guid": "6532b52f-9e2c-41dd-92d5-08fad64faa9a"
                        }
                    }
                """.trimIndent()
            ),
            // Messages received when a groupV2 title changes and the main device sends a message
            Pair(
                IncomingMessage(
                    version = "v1",
                    data = IncomingMessage.Data(
                        account = "+12222223333",
                        source = JsonAddress(
                            number = "+12222223333",
                            uuid = "6b4952b1-d50a-4498-91a2-6a274073e182"
                        ),
                        type = "CIPHERTEXT",
                        timestamp = 1634476410711,
                        sourceDevice = 1,
                        serverReceiverTimestamp = 1628134410966,
                        serverDeliverTimestamp = 1628134611321,
                        hasLegacyMessage = false,
                        hasContent = true,
                        unidentifiedSender = false,
                        syncMessage = JsonSyncMessage(
                            sent = JsonSentTranscriptMessage(
                                timestamp = 1634476410711,
                                expirationStartTimestamp = 0L,
                                message = JsonDataMessage(
                                    timestamp = 1634476410711,
                                    groupV2 = JsonGroupV2Info(
                                        id = "cSnOJU/LR4eD145CibXMNHGEwjr53iCWAaZd/xHPFQ4=",
                                        revision = 6,
                                        timer = 0,
                                    ),
                                    endSession = false,
                                    expiresInSeconds = 0,
                                    profileKeyUpdate = false,
                                    viewOnce = false,
                                ),
                                unidentifiedStatus = mapOf(
                                    "aa47f671-843e-4b45-8c2a-71add6529ec9" to true,
                                    "+15555555555" to true
                                ),
                                isRecipientUpdate = false,
                            ),
                            contactsComplete = false,
                        ),
                        serverGuid = "fb8d1c0d-ffe0-412e-ba2f-72086fed28da"
                    )
                ),
                """
                    {
                        "type": "IncomingMessage",
                        "version": "v1",
                        "data": {
                            "account": "+12222223333",
                            "source": {
                                "number": "+12222223333",
                                "uuid": "6b4952b1-d50a-4498-91a2-6a274073e182"
                            },
                            "type": "CIPHERTEXT",
                            "timestamp": 1634476410711,
                            "source_device": 1,
                            "server_receiver_timestamp": 1628134410966,
                            "server_deliver_timestamp": 1628134611321,
                            "has_legacy_message": false,
                            "has_content": true,
                            "unidentified_sender": false,
                            "sync_message": {
                                "sent": {
                                    "timestamp": 1634476410711,
                                    "expirationStartTimestamp": 0,
                                    "message": {
                                        "timestamp": 1634476410711,
                                        "groupV2": {
                                            "id": "cSnOJU/LR4eD145CibXMNHGEwjr53iCWAaZd/xHPFQ4=",
                                            "revision": 6,
                                            "timer": 0
                                        },
                                        "endSession": false,
                                        "expiresInSeconds": 0,
                                        "profileKeyUpdate": false,
                                        "viewOnce": false
                                    },
                                    "unidentifiedStatus": {
                                        "aa47f671-843e-4b45-8c2a-71add6529ec9": true,
                                        "+15555555555": true
                                    },
                                    "isRecipientUpdate": false
                                },
                                "contactsComplete": false
                            },
                            "server_guid": "fb8d1c0d-ffe0-412e-ba2f-72086fed28da"
                        }
                    }
                """.trimIndent()
            ),
        )
    }
}
