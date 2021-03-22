package drewcarlson.blockset

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

expect fun runBlocking(block: suspend CoroutineScope.() -> Unit)

class BlocksetTests {

    private lateinit var bdbService: BdbService

    @BeforeTest
    fun setUp() {
        bdbService = BdbService.createForTest(bdbAuthToken = BDB_CLIENT_TOKEN)
    }

    @Test
    fun testCurrency() = runBlocking {
        val btcMainnet = bdbService.getCurrency("bitcoin-mainnet:__native__")

        assertEquals("Bitcoin", btcMainnet.name)
        assertEquals("btc", btcMainnet.code)
        assertEquals("native", btcMainnet.type)

        delay(1100)

        val ethMainnet = bdbService.getCurrency("ethereum-mainnet:__native__")

        assertEquals("Ethereum", ethMainnet.name)
        assertEquals("eth", ethMainnet.code)
        assertEquals("native", ethMainnet.type)

        delay(1100)
    }

    @Test
    fun testAddressLookup() = runBlocking {
        val addresses = bdbService.addressLookup("ijustine.eth", "eth").embedded.addresses
        assertEquals(BdbAddress.Status.SUCCESS, addresses.first().status)
    }
}
