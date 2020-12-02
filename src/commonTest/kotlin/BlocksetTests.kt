package drewcarlson.blockset

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory
import kotlinx.coroutines.CoroutineScope
import kotlin.test.*

expect fun runBlocking(block: suspend CoroutineScope.() -> Unit)

class BlocksetTests {

    @Test
    fun test() = runBlocking {
    }
}
