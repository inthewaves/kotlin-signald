import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.inthewaves.kotlinsignald.Recipient
import org.inthewaves.kotlinsignald.Signal
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ExceptionWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.IncomingMessage
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ListenerState
import org.inthewaves.kotlinsignald.subscription.signalMessagesChannel

private inline fun measureHrTime(block: () -> Unit): Number {
    val start = process.hrtime()
    block()
    val end: dynamic = process.hrtime(start)
    val endMs: dynamic = end[0] * 1000 + end[1] / 1000000
    return endMs.unsafeCast<Number>()
}

suspend fun main(args: Array<String>) {
    println("args: ${args.asList()}")
    val actualArgs = process.argv.slice(2.. process.argv.lastIndex)
    println("args from process: $actualArgs")
    val signalClient = Signal.create(actualArgs.first())
    println("Account list: ${signalClient.listAccounts()}")

    println("Entering channel")
    coroutineScope {
        val channel = signalMessagesChannel(signalClient, Dispatchers.Default)
        for (msg in channel) {
            println("Received a message:\n$msg\n======================================")
            val (body, src) = when (msg) {
                is IncomingMessage -> msg.data.dataMessage?.body to msg.data.source!!
                is ExceptionWrapper, is ListenerState -> null to null
            }
            if (body != null && src != null) {
                signalClient.send(Recipient.forIndividual(src), "I am replying")
                delay(500L)
            }
        }
        println("GOT OUT OF THE LOOPING")
    }
    println("Exiting channel")
}
