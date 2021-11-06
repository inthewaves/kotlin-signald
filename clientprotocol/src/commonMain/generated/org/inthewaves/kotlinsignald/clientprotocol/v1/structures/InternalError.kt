// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol.v1.structures

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * an internal error in signald has occurred. typically these are things that "should never happen"
 * such as issues saving to the local disk, but it is also the default error type and may catch some
 * things that should have their own error type. If you find tht your code is depending on the
 * exception list for any particular behavior, please file an issue so we can pull those errors out to
 * a separate error type: https://gitlab.com/signald/signald/-/issues/new
 */
@Serializable
@SerialName("InternalError")
public data class InternalError(
    public override val version: String? = null,
    public override val data: Data,
    public override val error: Boolean? = false,
    public override val account: String? = null
) : ClientMessageWrapper() {
    @Serializable
    public data class Data(
        public val exceptions: List<String> = emptyList(),
        public val message: String? = null
    ) : ClientMessageWrapper.Data()
}