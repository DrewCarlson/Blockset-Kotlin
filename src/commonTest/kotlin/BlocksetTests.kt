package drewcarlson.blockset

import kotlinx.coroutines.CoroutineScope
import kotlin.test.*

expect fun runBlocking(block: suspend CoroutineScope.() -> Unit)

class BlocksetTests {

    val bdbService = BdbService.createForTest(bdbAuthToken = BDB_CLIENT_TOKEN)

    @Test
    fun test() = runBlocking {
        val btcMainnet = bdbService.getCurrency("bitcoin-mainnet:__native__")

        assertEquals("Bitcoin", btcMainnet.name)
        assertEquals("btc", btcMainnet.code)
        assertEquals("1855978125000000", btcMainnet.totalSupply)
        assertEquals("native", btcMainnet.type)
    }
}
