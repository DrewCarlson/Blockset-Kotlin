package drewcarlson.blockset

import drewcarlson.blockset.model.*
import io.ktor.client.*
import kotlin.jvm.JvmOverloads

private const val DEFAULT_BDB_BASE_URL = "api.blockset.com"

interface BdbService {

    companion object {
        @JvmOverloads
        public fun create(
            httpClient: HttpClient = HttpClient()
        ): BdbService = KtorBdbService(httpClient, DEFAULT_BDB_BASE_URL)

        @JvmOverloads
        public fun createForTest(
            bdbAuthToken: String,
            httpClient: HttpClient = HttpClient(),
            bdbBaseURL: String = DEFAULT_BDB_BASE_URL
        ): BdbService = KtorBdbService(httpClient, bdbBaseURL, bdbAuthToken)
    }

    public suspend fun getBlockchains(isMainnet: Boolean = true): BdbBlockchains

    public suspend fun getBlockchain(id: String): BdbBlockchain

    public suspend fun getCurrencies(blockchainId: String? = null): BdbCurrencies

    public suspend fun getCurrency(currencyId: String): BdbCurrency

    public suspend fun getOrCreateSubscription(
        subscription: BdbSubscription
    ): BdbSubscription

    public suspend fun getSubscription(id: String): BdbSubscription

    public suspend fun getSubscriptions(): BdbSubscriptions

    public suspend fun createSubscription(
        deviceId: String,
        endpoint: BdbSubscription.Endpoint,
        currencies: List<BdbSubscription.Currency>
    ): BdbSubscription

    public suspend fun updateSubscription(subscription: BdbSubscription): BdbSubscription

    public suspend fun deleteSubscription(id: String)

    public suspend fun getTransfers(
        blockchainId: String,
        addresses: List<String>,
        beginBlockNumber: ULong?,
        endBlockNumber: ULong?,
        maxPageSize: Int? = null
    ): List<BdbTransfer>

    public suspend fun getTransfer(transferId: String): BdbTransfer

    public suspend fun getTransactions(
        blockchainId: String,
        addresses: List<String>,
        beginBlockNumber: ULong?,
        endBlockNumber: ULong?,
        includeRaw: Boolean,
        includeProof: Boolean,
        maxPageSize: Int? = null
    ): BdbTransactions

    public suspend fun getTransaction(
        transactionId: String,
        includeRaw: Boolean,
        includeProof: Boolean
    ): BdbTransaction

    public suspend fun createTransaction(
        blockchainId: String,
        hashAsHex: String,
        tx: ByteArray
    ): Unit

    public suspend fun getBlocks(
        blockchainId: String,
        includeRaw: Boolean = true,
        includeTx: Boolean = false,
        includeTxRaw: Boolean = false,
        includeTxProof: Boolean = false,
        beginBlockNumber: ULong? = null,
        endBlockNumber: ULong? = null,
        maxPageSize: Int? = null
    ): List<BdbBlock>

    public suspend fun getBlock(
        blockId: String,
        includeTx: Boolean,
        includeTxRaw: Boolean,
        includeTxProof: Boolean
    ): BdbBlock

    public suspend fun getBlockWithRaw(blockId: String): BdbBlock
}
