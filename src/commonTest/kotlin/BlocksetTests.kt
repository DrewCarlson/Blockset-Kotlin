package drewcarlson.blockset

import kotlinx.coroutines.CoroutineScope
import kotlin.test.Test
import kotlin.test.assertEquals

expect fun runBlocking(block: suspend CoroutineScope.() -> Unit)

class BlocksetTests {

    val bdbService = BdbService.createForTest(bdbAuthToken = BDB_CLIENT_TOKEN)

    @Test
    fun testCurrency() = runBlocking {
        val btcMainnet = bdbService.getCurrency("bitcoin-mainnet:__native__")

        assertEquals("Bitcoin", btcMainnet.name)
        assertEquals("btc", btcMainnet.code)
        assertEquals("native", btcMainnet.type)

        val ethMainnet = bdbService.getCurrency("ethereum-mainnet:__native__")

        assertEquals("Ethereum", ethMainnet.name)
        assertEquals("eth", ethMainnet.code)
        assertEquals("native", ethMainnet.type)
    }
}
