import org.inthewaves.kotlinsignald.Signal
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ClientMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ExceptionWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.IncomingMessage
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonAddress
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonQuote
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ListenerState
import platform.posix.fputs
import platform.posix.stderr
import kotlin.system.exitProcess

private const val USAGE = "usage: pass in account ID registered with signald (E.164 format)\n" +
    "e.g.\n" +
    "    ./example-bot-linuxX64.kexe +1234568901"

/**
 * A bot that responds back with the message sent iff the message is in a 1-1 conversation (i.e. not a group chat).
 */
fun main(args: Array<String>) {
    if (args.size != 1) {
        println(USAGE)
        exitProcess(1)
    }
    val accountId = args.first()
    val signal = Signal(accountId)
    if (!signal.isRegisteredWithSignald) {
        println("error: $accountId is not registered with signald")
        println("available accounts: ${signal.listAccounts().accounts.map { it.accountId }}")
        exitProcess(1)
    }

    signal.subscribeAndConsumeBlocking { message ->
        val handleResult = handleMessage(message, signal)
        if (handleResult is HandleResult.Failure) {
            throw Error("Got HandleResult.Failure: ${handleResult.failureMessage}")
        }
    }
}

sealed class HandleResult {
    object Success : HandleResult()
    class Failure(val failureMessage: String) : HandleResult()
}

/**
 * @return Whether the coroutine should cancel
 */
private fun handleMessage(
    message: ClientMessageWrapper,
    signal: Signal
): HandleResult {
    when (message) {
        is IncomingMessage -> {
            val data: IncomingMessage.Data = message.data
            println("Received message: $data")
            val source: JsonAddress? = data.source
            if (source == null) {
                fputs("Incoming message is missing a source", stderr)
                return HandleResult.Success
            }

            val body: String? = data.dataMessage?.body
            if (body.isNullOrBlank()) {
                fputs("Incoming message is missing a body; not responding", stderr)
                return HandleResult.Success
            }

            if (body == "crash") {
                throw Error("I am crashing now!")
            } else if (body == "fail") {
                return HandleResult.Failure("Fail message received")
            }

            if (data.dataMessage?.groupV2 != null || data.dataMessage?.group != null) {
                fputs("Incoming message is to a group; not responding", stderr)
                return HandleResult.Success
            }

            val sendResponse = signal.send(
                recipient = Signal.Recipient.Individual(source),
                messageBody = "You sent me this message:\n$body",
                quote = JsonQuote(
                    id = data.timestamp!!,
                    author = source,
                    text = body
                )
            )
            fputs("Sent back a reply: $sendResponse", stderr)
            return HandleResult.Success
        }
        is ListenerState -> {
            return if (message.data.connected == true) {
                println("Listening to messages")
                HandleResult.Success
            } else {
                HandleResult.Failure("ListenerState connected is not true")
            }
        }
        is ExceptionWrapper -> {
            fputs("warning: received exception: ${message.data}", stderr)
            return HandleResult.Success
        }
    }
}
