package org.inthewaves.kotlinsignald.subscription

import org.inthewaves.kotlinsignald.IncomingMessageSubscription
import org.inthewaves.kotlinsignald.SignaldClient
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ClientMessageWrapper

internal actual suspend fun getSubscription(signaldClient: SignaldClient): IncomingMessageSubscription {
    return signaldClient.subscribeSuspend()
}

internal actual suspend fun getNextMessage(subscription: IncomingMessageSubscription): ClientMessageWrapper? {
    return subscription.nextMessageSuspend()
}
