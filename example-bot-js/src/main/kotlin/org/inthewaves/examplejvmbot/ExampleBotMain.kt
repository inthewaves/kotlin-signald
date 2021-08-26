/*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.await
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException
 */
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import net.Socket
import org.inthewaves.kotlinsignald.Recipient
import org.inthewaves.kotlinsignald.Signal
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ExceptionWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.IncomingMessage
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ListenerState
import org.inthewaves.kotlinsignald.subscription.signalMessagesChannel

fun getSocket(): Socket {
    return net.createConnection("/var/run/signald/signald.sock")
}

/*
@JsExport
class What {
    suspend fun hello(): String {
        delay(500L)
        return "A"
    }
}
 */

private inline fun measureHrTime(block: () -> Unit): Number {
    val start = process.hrtime()
    block()
    val end: dynamic = process.hrtime(start)
    val endMs: dynamic = end[0] * 1000 + end[1] / 1000000
    return endMs.unsafeCast<Number>()
}

suspend fun main() {
    val signalClient = Signal.create("+123")
    println("Account list: ${signalClient.listAccounts()}")

    println("Entering channel")
    coroutineScope {
        val channel = signalMessagesChannel(signalClient, Dispatchers.Default)
        for (msg in channel) {
            println("Msg:\n$msg\n======================================")
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
