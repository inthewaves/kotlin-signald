package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.GenerateLinkingUri
import org.inthewaves.kotlinsignald.clientprotocol.v1.requests.JsonMessageWrapper

/**
 * Generate a linking URI. Typically this is QR encoded and scanned by the primary device. Submit
 * the returned session_id with a finish_link request.
 */
@Serializable
@SerialName("generate_linking_uri")
public data class GenerateLinkingURIRequest(
    /**
     * The identifier of the server to use. Leave blank for default (usually Signal production
     * servers but configurable at build time)
     */
    public val server: String? = null
) : SignaldRequestBodyV1<LinkingURI>() {
    internal override val responseWrapperSerializer: KSerializer<GenerateLinkingUri>
        get() = GenerateLinkingUri.serializer()

    internal override val responseDataSerializer: KSerializer<LinkingURI>
        get() = LinkingURI.serializer()

    internal override fun getTypedResponseOrNull(responseWrapper: JsonMessageWrapper<*>):
        LinkingURI? = if (responseWrapper is GenerateLinkingUri && responseWrapper.data is
        LinkingURI
    ) {
        responseWrapper.data
    } else {
        null
    }
}
