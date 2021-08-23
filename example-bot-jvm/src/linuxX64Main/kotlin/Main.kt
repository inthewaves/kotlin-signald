import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.inthewaves.examplejvmbot.runBot

fun main(args: Array<String>) {
    return runBlocking { runBot(args, newSingleThreadContext("bot runner")) }
}