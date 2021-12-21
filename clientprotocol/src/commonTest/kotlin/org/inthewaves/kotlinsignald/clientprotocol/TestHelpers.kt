package org.inthewaves.kotlinsignald.clientprotocol

inline fun <reified T : Throwable> assertThrows(executable: () -> Unit): T {
    try {
        executable.invoke()
    } catch (e: Throwable) {
        if (e is T) {
            return e
        }

        throw AssertionError(
            "Unexpected exception type thrown (expected: ${T::class.simpleName}; got ${e::class.simpleName}. " +
                "Exception stacktrace:\n" + e.stackTraceToString()
        )
    }
    throw AssertionError("Expected ${T::class.simpleName} to be thrown, but nothing was thrown.")
}
