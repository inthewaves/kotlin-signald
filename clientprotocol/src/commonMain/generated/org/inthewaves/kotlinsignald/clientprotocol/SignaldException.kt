// File is generated by ./gradlew generateSignaldClasses --- do not edit unless reformatting
package org.inthewaves.kotlinsignald.clientprotocol

public expect open class SignaldException : Exception {
    public constructor()

    public constructor(message: String?)

    public constructor(message: String?, cause: Throwable?)

    public constructor(cause: Throwable?)
}
