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
import NodeJS.get
import fs.NoParamCallback
import kotlinx.coroutines.await
import net.Socket
import org.inthewaves.kotlinsignald.PersistentSocketWrapper
import org.inthewaves.kotlinsignald.Signal
import org.inthewaves.kotlinsignald.SocketWrapper
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.ListAccountsRequest
import org.inthewaves.kotlinsignald.clientprotocol.v1.structures.VersionRequest
import kotlin.js.Promise

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
    println(greeting("nosejs-test"))

    val socketWrapper = SocketWrapper.createSuspend()

    println("Sending version request:")

    val timeTaken = measureHrTime {
        VersionRequest().submitSuspend(socketWrapper)
        ListAccountsRequest().submitSuspend(socketWrapper)
    }
    println("Time: $timeTaken ms")

    val persistentSocket = PersistentSocketWrapper.createSuspend("/var/run/signald/signald.sock")
    try {
        val timeTaken2 = measureHrTime {
            VersionRequest().submitSuspend(socketWrapper)
            ListAccountsRequest().submitSuspend(socketWrapper)
        }
        println("Time for persistent socket: $timeTaken2 ms")
    } finally {
        persistentSocket.close()
    }

    val signalClient = Signal.create("+123")
    println("Account list: ${signalClient.listAccounts()}")

    return

    // println("Sleeping for 5000ms")
    // sleep(5000)
    // println("Done Sleeping for 5000ms")

    /*
    val channel = Channel<String>(Channel.UNLIMITED)
    val scope = CoroutineScope(Dispatchers.Main)
    var done = false
    scope.launch {
        delay(2500L)
        channel.trySend("Abc")
        done = true
    }

    while (true) {
        val result = channel.tryReceive()
        if (result.isSuccess) {
            println("LOOPWHILE SUCCESS: I got success: ${result.getOrThrow()}")
            break
        } else {
            // println("LOOPWHILE FAILURE: ${result.isFailure} ${result.exceptionOrNull()}, retrying")
            sleep(100)
        }
    }
     */


    /*
    println("Beginning JS waiting game")
    js("""
        require('deasync').loopWhile(function(){return !done;});
    """)
     */

    println(process.env["XDG_RUNTIME_DIR"])


    val fsHandler: NoParamCallback = {
        println("Err no exception: $it")
    }

    /*
    js("""
        fs.access("/var/run/signald/signald.socket", fs.constants.R_OK | fs.constants.W_OK, fsHandler);
        """)
     */
    /*
    fs.access("/var/run/signald/signald.sock", R_OK) {
        println("Second handler: Err no exception: $it")
    }
     */

    val connectHandler = { _: Any -> println("connected") }
    fun createDataHandler(socket: Socket, onFinish: () -> Unit) : (Buffer) -> Unit = { data: Buffer ->
        val dataString = "$data"
        println("Got $dataString")

        if (dataString.contains("version")) {
            println("It is a version string!")
            socket.write("""{"type":"list_accounts","version":"v1"}""" + "\n")
        } else {
            println("Destroying!")
            socket.destroy()
            onFinish()
        }
    }
    fun createErrorHandler(socket: Socket, onException: (Throwable) -> Unit = { throw it }) = { error: Any ->
        val type = jsTypeOf(error)
        if (error is Throwable) {
            val result = js("error instanceof Error")
            println("Error is Throwable!")
            println("error instanceof Error: $result")
        }
        val errorString = error.toString()
        val doesntExist = errorString.contains("ENOENT")
        val cantAccess = errorString.contains("EACCES")
        socket.destroy()
        onException(
            Error(
                "Got error $error (type: $type, ${error::class.js.name}, " +
                    "doesntExist: $doesntExist, cantAcces: $cantAccess)"
            )
        )
    }

    /*
    println("I am going to suspend!")
    suspendCancellableCoroutine<String> { cont ->
        val socket = getSocket()
        socket.on("connect", connectHandler)
            .on("data", createDataHandler(socket) { cont.resume("a") {} })
            .on("error", createErrorHandler(socket) { cont.resumeWithException(it) })
    }
    println("I finished going to suspend!")
     */

    println("I am going to promisify!")
    val promise = Promise<Unit> { resolve, reject ->
        val socket = getSocket()
        println("socket remote addr: ${socket.remoteAddress}")
        println("socket local addr: ${socket.localAddress}")
        println("socket local addr: ${socket.eventNames().asList()}")
        println("socket addr: ${JSON.stringify(socket.address())}")
        val errorListener = createErrorHandler(socket) { reject(it) }

        socket.on("connect", connectHandler)
            .on("data", createDataHandler(socket) {
                socket.removeListener("error", errorListener)
                socket.destroy()
                resolve(Unit)
            })
            .on("error", errorListener)
    }
    promise.await()
    println("I finished going to promisify!")

    println("I am going to promisify again!!")
    val promiseA = Promise<Unit> { resolve, reject ->
        val socket = getSocket()
        val errorListener = createErrorHandler(socket) { reject(it) }
        var counter = 0
        socket
            .on("connect") { _: Any ->
                socket.write("""{"type":"list_accounts","version":"v1"}""" + "\n")
                socket.on("data") { buffer: Buffer ->
                    println("received $buffer")
                    counter++
                    if (counter >= 2) {
                        socket.destroy()
                        resolve(Unit)
                    }
                }
            }
            .on("error", errorListener)
    }
    promiseA.await()
    println("I finished going to promisify twice!")

    val exector: (resolve: (String) -> Unit, reject: (Throwable) -> Unit) -> Unit = { resolve, reject ->
        resolve("HI")
    }
    val x = Promise<String>(exector)

    println("Infinite looping")
    println("I am at the end of main, why am I not exiting?+ I got")


}

fun greeting(name: String) =
    "Hello, $name"