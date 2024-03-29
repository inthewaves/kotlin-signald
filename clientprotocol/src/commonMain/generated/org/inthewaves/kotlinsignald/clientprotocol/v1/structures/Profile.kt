// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Information about a Signal user
 */
@Serializable
public data class Profile(
    /**
     * The user's name from local contact names if available, or if not in contact list their Signal
     * profile name
     */
    public val name: String? = null,
    /**
     * path to avatar on local disk
     */
    public val avatar: String? = null,
    public val address: JsonAddress? = null,
    public val capabilities: Capabilities? = null,
    /**
     * color of the chat with this user
     */
    public val color: String? = null,
    public val about: String? = null,
    public val emoji: String? = null,
    /**
     * The user's name from local contact names
     */
    @SerialName("contact_name")
    public val contactName: String? = null,
    /**
     * The user's Signal profile name
     */
    @SerialName("profile_name")
    public val profileName: String? = null,
    @SerialName("inbox_position")
    public val inboxPosition: Int? = null,
    @SerialName("expiration_time")
    public val expirationTime: Int? = null,
    /**
     * *base64-encoded* mobilecoin address. Note that this is not the traditional MobileCoin address
     * encoding. Clients are responsible for converting between MobileCoin's custom base58 on the
     * user-facing side and base64 encoding on the signald side. If unset, null or an empty string,
     * will empty the profile payment address
     */
    @SerialName("mobilecoin_address")
    public val mobilecoinAddress: String? = null,
    /**
     * currently unclear how these work, as they are not available in the production Signal apps
     */
    @SerialName("visible_badge_ids")
    public val visibleBadgeIds: List<String> = emptyList()
) : SignaldResponseBodyV1()
