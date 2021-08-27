package org.inthewaves.kotlinsignald.subscription

import org.inthewaves.kotlinsignald.IncomingMessageSubscription

public expect class Subscription : IncomingMessageSubscription {
    public val accountId: String

    public override fun close()
}
