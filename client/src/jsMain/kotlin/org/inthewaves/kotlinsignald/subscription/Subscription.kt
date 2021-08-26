package org.inthewaves.kotlinsignald.subscription

import org.inthewaves.kotlinsignald.PersistentSocketWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ClientMessageWrapper

public class Subscription internal constructor(
    public val accountId: String,
    private val persistentSocket: PersistentSocketWrapper,
    initialMessages: Collection<ClientMessageWrapper>
) {

}