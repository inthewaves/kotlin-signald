package org.inthewaves.kotlinsignald.clientprotocol

import kotlinx.serialization.json.JsonObject

/**
 * An exception that is thrown if the resulting JSON can't be deserialized or the socket returns an
 * error response.
 */
public open class RequestFailedException(
    /**
     * The raw JSON string from the socket that caused the error.
     */
    public val responseJsonString: String? = null,
    public val errorBody: JsonObject? = null,
    public val errorType: String? = null,
    public val exception: String? = null,
    extraMessage: String? = null,
    cause: Throwable? = null
) : SignaldException(
    getErrorMessageString(
        responseJsonString, errorBody, errorType, exception,
        extraMessage, cause
    ),
    cause
) {
    public val isRateLimitException: Boolean
        get() = message?.let {
            it.contains("RateLimitException") ||
                it.contains("Rate limit exceeded: 413")
        } ?: false

    public companion object {
        private fun getErrorMessageString(
            responseJsonString: String? = null,
            errorBody: JsonObject? = null,
            errorType: String? = null,
            exception: String? = null,
            extraMessage: String? = null,
            cause: Throwable? = null
        ) = buildString {
            append("Request failed")
            if (extraMessage != null) {
                append(" (")
                append(extraMessage)
                append(") ")
            }
            if (cause?.message != null) {
                append(" (cause: ")
                append(cause.message)
                append(") ")
            }
            if (exception != null) {
                append(" (exception: ")
                append(exception)
                append(")")
            }
            if (errorType != null) {
                append(", error type ")
                append(errorType)
            }
            val errorMessage = errorBody?.get("message")
            if (errorMessage != null) {
                append(": ")
                append(errorMessage)
            }
        }
    }
}
