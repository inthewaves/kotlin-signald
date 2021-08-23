package org.inthewaves.examplejvmbot

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import org.inthewaves.kotlinsignald.Signal
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ClientMessageWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ExceptionWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.IncomingMessage
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonAddress
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.JsonQuote
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ListenerState
import org.inthewaves.kotlinsignald.subscription.signalMessagesChannel
import org.inthewaves.kotlinsignald.subscription.signalMessagesSharedFlow
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

private const val USAGE = "usage: pass in account ID registered with signald (E.164 format), and " +
    "optionally one of \"flow\" or \"channel\""

enum class ReceiverType {
    CHANNEL, FLOW
}

/**
 * A bot that responds back with the message sent iff the message is in a 1-1 conversation (i.e. not a group chat).
 */
suspend fun main(args: Array<String>) {
    if (args.size !in 1..2) {
        System.err.println(USAGE)
        exitProcess(1)
    }
    val accountId = args.first()
    val signal = Signal(accountId)
    if (!signal.isRegisteredWithSignald) {
        System.err.println("error: $accountId is not registered with signald")
        System.err.println("available accounts: ${signal.listAccounts().accounts.map { it.accountId }}")
        exitProcess(1)
    }

    val receiverType = if (args.size == 2) {
        val upperType = args.last().uppercase()
        try {
            ReceiverType.valueOf(upperType)
        } catch (e: IllegalArgumentException) {
            System.err.println(
                "invalid receiver type ${upperType}. " +
                    "Valid types: ${ReceiverType.values().map { it.name.uppercase() }}"
            )
            exitProcess(1)
        }
    } else {
        // default
        ReceiverType.CHANNEL
    }

    println("Starting example bot with type $receiverType. It will reply back with the text that is sent to it.")
    when (receiverType) {
        ReceiverType.CHANNEL -> coroutineScope {
            val channel = signalMessagesChannel(signal)
            for (message in channel) {
                val handleResult = handleMessage(message, signal)
                if (handleResult is HandleResult.Failure) {
                    channel.cancel(CancellationException(handleResult.failureMessage))
                }
            }
        }
        ReceiverType.FLOW -> coroutineScope {
            val flow = signalMessagesSharedFlow(signal)
            // note: SharedFlow can have multiple collectors / subscribers
            flow.collect { message ->
                val handleResult = handleMessage(message, signal)
                if (handleResult is HandleResult.Failure) {
                    cancel(message = handleResult.failureMessage)
                }
            }
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
private suspend fun handleMessage(
    message: ClientMessageWrapper,
    signal: Signal
): HandleResult {
    when (message) {
        is IncomingMessage -> {
            val data: IncomingMessage.Data = message.data
            println("Received message: $data")
            val source: JsonAddress? = data.source
            if (source == null) {
                System.err.println("Incoming message is missing a source")
                return HandleResult.Success
            }

            val body: String? = data.dataMessage?.body
            if (body.isNullOrBlank()) {
                System.err.println("Incoming message is missing a body; not responding")
                return HandleResult.Success
            }

            if (body == "crash") {
                throw IOException("I am crashing now!")
            } else if (body == "fail") {
                return HandleResult.Failure("Fail message received")
            }

            if (data.dataMessage?.groupV2 != null || data.dataMessage?.group != null) {
                System.err.println("Incoming message is to a group; not responding")
                return HandleResult.Success
            }

            delay(TimeUnit.SECONDS.toMillis(2))
            val sendResponse = signal.send(
                recipient = Signal.Recipient.Individual(source),
                messageBody = "You sent me this message:\n$body",
                quote = JsonQuote(
                    id = data.timestamp!!,
                    author = source,
                    text = body
                )
            )
            println("Sent back a reply: $sendResponse")
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
            System.err.println("warning: received exception: ${message.data}")
            return HandleResult.Success
        }
    }
}