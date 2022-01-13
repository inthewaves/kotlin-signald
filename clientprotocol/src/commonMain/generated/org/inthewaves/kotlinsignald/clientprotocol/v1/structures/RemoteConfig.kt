// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.Serializable

/**
 * A remote config (feature flag) entry.
 */
@Serializable
public data class RemoteConfig(
    /**
     * The name of this remote config entry. These names may be prefixed with the platform type
     * ("android.", "ios.", "desktop.", etc.) Typically, clients only handle the relevant configs for
     * its platform, hardcoding the names it cares about handling and ignoring the rest.
     *
     * Example: desktop.mediaQuality.levels
     */
    public val name: String? = null,
    /**
     * The value for this remote config entry. Even though this is a string, it could be a boolean
     * as a string, an integer/long value, a comma-delimited list, etc. Clients usually consume this by
     * hardcoding the feature flagsit should track in the app and assuming that the server will send
     * the type that the client expects. If an unexpected type occurs, it falls back to a default
     * value.
     *
     * Example: 1:2,61:2,81:2,82:2,65:2,31:2,47:2,41:2,32:2,385:2,971:2,974:2,49:2,33:2,*:1
     */
    public val value: String? = null
)
