package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException
import org.inthewaves.kotlinsignald.clientprotocol.SignaldJson
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.assertThrows
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class ExceptionsTest {
    @Test
    fun testGetIdentitiesException() {
        val getIdentitiesRequest = GetIdentitiesRequest(account = "+123", address = JsonAddress(number = "+123"))
        val socketCommunicator = object : SocketCommunicator {
            override fun close() {}

            override fun submit(request: String): String {
                return """{"type":"get_identities","id":"${getIdentitiesRequest.id}","error":{"account":"+123","message":"account not found"},"error_type":"NoSuchAccountError"}"""
            }

            override fun readLine(): String = error("not used")
        }
        val exception = assertThrows<RequestFailedException> { getIdentitiesRequest.submit(socketCommunicator) }
        assertNotNull(exception.errorBody, "missing exception error body. exception message: ${exception.message}")
        val deserializeStrat = SignaldJson.serializersModule.getPolymorphic(TypedExceptionV1::class, exception.errorType)
        assertNotNull(deserializeStrat, "failed to get deserialization strategy. Error type is ${exception.errorType}")
        val typedExceptionBody: TypedExceptionV1 = SignaldJson.decodeFromJsonElement(
            deserializeStrat,
            exception.errorBody!!
        )
        assertIs<NoSuchAccountError>(typedExceptionBody)
    }
}
