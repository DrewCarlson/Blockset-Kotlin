package drewcarlson.blockset

import drewcarlson.blockset.model.*
import io.ktor.client.*
import kotlin.jvm.JvmOverloads

private const val DEFAULT_BDB_BASE_URL = "api.blockset.com"

interface BdbService {

    companion object {
        public fun create(): BdbService = create(HttpClient())

        public fun create(
            httpClient: HttpClient = HttpClient()
        ): BdbService = KtorBdbService(httpClient, DEFAULT_BDB_BASE_URL)

        public fun create(
            httpClient: HttpClient,
            authProvider: AuthProvider
        ): BdbService = KtorBdbService(httpClient, authProvider = authProvider)

        public fun create(
            authProvider: AuthProvider
        ): BdbService = KtorBdbService(HttpClient(), authProvider = authProvider)

        public fun createForTest(bdbAuthToken: String): BdbService =
            KtorBdbService(HttpClient(), DEFAULT_BDB_BASE_URL, AuthProvider.static(bdbAuthToken))
        
        @JvmOverloads
        public fun createForTest(
            bdbAuthToken: String,
            httpClient: HttpClient,
            bdbBaseURL: String = DEFAULT_BDB_BASE_URL
        ): BdbService = KtorBdbService(httpClient, bdbBaseURL, AuthProvider.static(bdbAuthToken))
    }

    interface AuthProvider {

        companion object : AuthProvider {
            fun static(token: String): AuthProvider =
                object : AuthProvider {
                    override fun readUserJwt(): String = token
                }
        }

        data class TokenDetails(
            val currentTimeSeconds: Long,
            val expirationTimeSeconds: Long,
        )

        fun newTokenDetails(): TokenDetails = error("Not implemented")

        fun signData(data: String): String = error("Not implemented")

        fun readPubKey(): String = ""
        fun readDeviceId(): String = ""
        fun readClientJwt(): String? = null

        fun readUserJwt(): String? = null
        fun saveUserJwt(jwt: String) = Unit
    }

    public suspend fun createUserToken(
        clientToken: String,
        deviceId: String,
        pubKey: String,
        signature: String,
    ): BdbUserTokenResult

    public suspend fun getBlockchains(testnet: Boolean = false): BdbBlockchains

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
        maxPageSize: Int? = null,
        mergeCurrencies: Boolean = false,
    ): List<BdbTransfer>

    public suspend fun getTransfer(
        transferId: String,
        mergeCurrencies: Boolean = false,
    ): BdbTransfer

    public suspend fun getTransactions(
        blockchainId: String,
        addresses: List<String>,
        beginBlockNumber: ULong = 0u,
        endBlockNumber: ULong? = null,
        startTimestamp: ULong? = null,
        endTimestamp: ULong? = null,
        includeRaw: Boolean = false,
        includeProof: Boolean = false,
        includeTransfers: Boolean = true,
        includeCalls: Boolean = false,
        maxPageSize: Int? = null,
        mergeCurrencies: Boolean = false,
    ): BdbTransactions

    public suspend fun getTransaction(
        transactionId: String,
        includeRaw: Boolean,
        includeProof: Boolean,
        mergeCurrencies: Boolean = false,
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
    ): BdbBlockList

    public suspend fun getBlock(
        blockId: String,
        includeTx: Boolean,
        includeTxRaw: Boolean,
        includeTxProof: Boolean
    ): BdbBlock

    public suspend fun getBlockWithRaw(blockId: String): BdbBlock

    public suspend fun addressLookup(
        domainName: String,
        vararg currencyCodes: String
    ): BdbAddressesResult

    public suspend fun addressLookup(
        domainName: String,
        currencyCodeList: List<String>
    ): BdbAddressesResult
}
