package org.inthewaves.kotlinsignald.serializers

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

@Serializable
private data class ForUuidTest(
    @Serializable(UUIDSerializer::class)
    val uuid: UUID,
)

internal class UUIDSerializerTest {
    @Test
    fun serialize() {
        val uuid = UUID.fromString("44f08146-87b7-49cb-ace0-984ac2fdd4c3")
        val struct = """{"uuid":"$uuid"}"""
        assertEquals(ForUuidTest(uuid), Json.decodeFromString<ForUuidTest>(struct))
        assertEquals(ForUuidTest(uuid), Json.decodeFromString(ForUuidTest.serializer(), struct))
    }
}
