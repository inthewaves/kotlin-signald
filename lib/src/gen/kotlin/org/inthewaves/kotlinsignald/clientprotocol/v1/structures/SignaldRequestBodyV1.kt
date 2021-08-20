package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import org.inthewaves.kotlinsignald.SignaldJson
import org.inthewaves.kotlinsignald.clientprotocol.RequestFailedException
import org.inthewaves.kotlinsignald.clientprotocol.SocketCommunicator
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.UnexpectedError
import java.io.IOException

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 *
 * A base class for requests. This class is only used for serializing requests to the signald
 * socket; the [ResponseWrapper] type variable represents the response JSON structure.
 */
@Serializable
public sealed class SignaldRequestBodyV1<ResponseWrapper : JsonMessageWrapper<ResponseData>,
    ResponseData> {
    protected abstract val responseWrapperSerializer: KSerializer<ResponseWrapper>

    protected abstract val responseDataSerializer: KSerializer<ResponseData>

    /**
     * The version to include in the request. As this class won't be used to deserialize the
     * response, the [Required] annotation is being used to force this field to be serialized
     */
    @Required
    public val version: String = "v1"

    /**
     * The id to include in the request. This is expected to be present in the response JSON.
     */
    @Required
    public val id: String = """${System.currentTimeMillis()}"""

    /**
     * A function to resolve the response body by verifying the type of the response and returning a
     * non-null value iff the wrapper and data is the right type. This is desirable due to type
     * erasure.
     */
    public abstract fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        ResponseData?

    /**
     * @throws RequestFailedException if the signald socket sends a bad or error response, or unable
     * to serialize our request
     * @throws IOException if an I/O error occurs during socket communication
     */
    @Throws(IOException::class)
    public fun submit(socketCommunicator: SocketCommunicator): ResponseData =
        submit(socketCommunicator, id)

    /**
     * Marked as internal so tests can access. Normal API consumers should use the one-parameter
     * overload.
     *
     * @throws RequestFailedException if the signald socket sends a bad or error response, or unable
     * to serialize our request
     * @throws IOException if an I/O error occurs during socket communication
     * @param id The id to include in the request. This is expected to be present in the response
     * JSON
     */
    @Throws(IOException::class)
    internal open fun submit(socketCommunicator: SocketCommunicator, id: String = this.id):
        ResponseData {
        val requestJson = try {
            SignaldJson.encodeToString(
                serializer(
                    responseWrapperSerializer,
                    responseDataSerializer
                ),
                this
            )
        } catch (e: SerializationException) {
            throw RequestFailedException(
                responseJsonString = null,
                cause = e,
                extraMessage = "failed to serialize our request"
            )
        }
        val responseJson = socketCommunicator.submit(requestJson)
        val response: JsonMessageWrapper<*> = try {
            SignaldJson.decodeFromString(
                JsonMessageWrapper.serializer(responseDataSerializer),
                responseJson
            )
        } catch (e: SerializationException) {
            throw RequestFailedException(responseJsonString = responseJson, cause = e)
        }
        if (response is UnexpectedError) {
            throw RequestFailedException(
                responseJsonString = responseJson,
                errorBody =
                response.data,
                errorType = response.errorType, exception = response.exception,
                extraMessage = "unexpected error"
            )
        }
        if (response.id != id) {
            throw RequestFailedException(
                responseJsonString = responseJson,
                extraMessage =
                """response has unexpected ID: ${response.id} (expected $id)"""
            )
        }
        if (response.version != null && response.version != version) {
            throw RequestFailedException(
                responseJsonString = responseJson,
                extraMessage =
                """response has unexpected version: ${response.version} (expected $version)"""
            )
        }
        if (!response.isSuccessful) {
            throw RequestFailedException(
                responseJsonString = responseJson,
                errorBody =
                response.error,
                errorType = response.errorType, exception = response.exception
            )
        }
        return getTypedResponseOrNull(response) ?: throw RequestFailedException(
            responseJsonString =
            responseJson,
            extraMessage =
            """response failed verification (wrapper type: ${response::class.java.simpleName}, data type: ${response.data!!::class.java.simpleName})"""
        )
    }
}
