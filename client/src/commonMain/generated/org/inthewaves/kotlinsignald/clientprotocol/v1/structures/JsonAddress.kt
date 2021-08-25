package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

@Serializable
public data class JsonAddress(
    /**
     * An e164 phone number, starting with +. Currently the only available user-facing Signal
     * identifier.
     *
     * Example: "+13215551234"
     */
    public val number: String? = null,
    /**
     * A UUID, the unique identifier for a particular Signal account.
     */
    public val uuid: String? = null,
    public val relay: String? = null
) : SignaldResponseBodyV1()
