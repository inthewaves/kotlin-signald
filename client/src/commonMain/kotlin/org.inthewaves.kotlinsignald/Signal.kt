package org.inthewaves.kotlinsignald

import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.Account

public class Signal(public val accountId: String, public val socketPath: String? = null) {
    public var accountInfo: Account? = null
        private set
}