package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Generated from signald version 0.14.1+git2021-08-13r7dde35de.21
 *
 * a Signal server
 */
@Serializable
public data class Server(
    /**
     * A unique identifier for the server, referenced when adding accounts. Must be a valid UUID.
     * Will be generated if not specified when creating.
     */
    public val uuid: String? = null,
    public val proxy: String? = null,
    public val ca: String? = null,
    @SerialName("service_url")
    public val serviceUrl: String? = null,
    @SerialName("cdn_urls")
    public val cdnUrls: List<ServerCDN> = emptyList(),
    @SerialName("contact_discovery_url")
    public val contactDiscoveryUrl: String? = null,
    @SerialName("key_backup_url")
    public val keyBackupUrl: String? = null,
    @SerialName("storage_url")
    public val storageUrl: String? = null,
    /**
     * base64 encoded ZKGROUP_SERVER_PUBLIC_PARAMS value
     */
    @SerialName("zk_param")
    public val zkParam: String? = null,
    @SerialName("unidentified_sender_root")
    public val unidentifiedSenderRoot: String? = null
)
