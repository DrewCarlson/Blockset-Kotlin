package drewcarlson.blockset

import drewcarlson.blockset.model.*
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.readText
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.native.concurrent.SharedImmutable

private const val DEFAULT_BDB_BASE_URL = "api.blockset.com"

@SharedImmutable
internal val json = Json {
    isLenient = true
    ignoreUnknownKeys = true
    coerceInputValues = true
    useAlternativeNames = false
}

internal class KtorBdbService(
    httpClient: HttpClient,
    bdbBaseURL: String = DEFAULT_BDB_BASE_URL,
    authProvider: BdbService.AuthProvider = BdbService.AuthProvider,
) : BdbService {

    private val http = httpClient.config {
        install(JsonFeature) {
            serializer = KotlinxSerializer(json)
        }

        install(BdbAuthentication) {
            setAuthProvider(authProvider)
            setUserTokenFactory(::createUserToken)
        }

        defaultRequest {
            url.host = bdbBaseURL
            url.protocol = URLProtocol.HTTPS
        }
    }

    public override suspend fun createUserToken(
        clientToken: String,
        deviceId: String,
        pubKey: String,
        signature: String
    ): BdbUserTokenResult = createUserToken(http, clientToken, deviceId, pubKey, signature)

    public override suspend fun getBlockchains(testnet: Boolean): BdbBlockchains =
        http.get("/blockchains") {
            parameter("testnet", testnet)
        }

    public override suspend fun getBlockchain(id: String): BdbBlockchain =
        http.get("/blockchains/$id")

    public override suspend fun getCurrencies(blockchainId: String?): BdbCurrencies =
        http.get("/currencies") {
            parameter("blockchain_id", blockchainId)
        }

    public override suspend fun getCurrency(currencyId: String): BdbCurrency =
        http.get("/currencies/$currencyId")

    public override suspend fun getOrCreateSubscription(
        subscription: BdbSubscription
    ): BdbSubscription =
        http.get("/subscriptions/${subscription.subscriptionId}")

    public override suspend fun getSubscription(id: String): BdbSubscription =
        http.get("/subscriptions/$id")

    public override suspend fun getSubscriptions(): BdbSubscriptions =
        http.get("/subscriptions")

    public override suspend fun createSubscription(
        deviceId: String,
        endpoint: BdbSubscription.Endpoint,
        currencies: List<BdbSubscription.Currency>
    ): BdbSubscription =
        http.post("/subscriptions") {
            body = BdbSubscription.Create(
                deviceId, endpoint, currencies
            )
        }

    public override suspend fun updateSubscription(subscription: BdbSubscription): BdbSubscription =
        http.put("/subscriptions/${subscription.subscriptionId}") {
            body = subscription
        }

    public override suspend fun deleteSubscription(id: String) =
        http.delete<Unit>("/subscriptions/$id")

    public override suspend fun getTransfers(
        blockchainId: String,
        addresses: List<String>,
        beginBlockNumber: ULong?,
        endBlockNumber: ULong?,
        maxPageSize: Int?
    ): List<BdbTransfer> =
        http.get("/transfers") {
            parameter("blockchain_id", blockchainId)
            parameter("start_height", beginBlockNumber)
            parameter("end_height", endBlockNumber)
            parameter("max_page_size", maxPageSize)
            parameter("address", addresses)
        }

    public override suspend fun getTransfer(transferId: String): BdbTransfer =
        http.get("/transfers/$transferId")

    override suspend fun getTransactions(
        blockchainId: String,
        addresses: List<String>,
        beginBlockNumber: ULong,
        endBlockNumber: ULong?,
        startTimestamp: ULong?,
        endTimestamp: ULong?,
        includeRaw: Boolean,
        includeProof: Boolean,
        includeTransfers: Boolean,
        includeCalls: Boolean,
        maxPageSize: Int?
    ): BdbTransactions =
        http.get("/transactions") {
            parameter("blockchain_id", blockchainId)
            parameter("include_proof", includeProof)
            parameter("include_raw", includeRaw)
            parameter("include_calls", includeCalls)
            parameter("include_transfers", includeTransfers)
            parameter("start_height", beginBlockNumber)
            parameter("end_height", endBlockNumber)
            parameter("max_page_size", maxPageSize)
            parameter("start_ts", startTimestamp)
            parameter("end_ts", endTimestamp)
            parameter("address", addresses.joinToString(","))
        }

    public override suspend fun getTransaction(
        transactionId: String,
        includeRaw: Boolean,
        includeProof: Boolean
    ): BdbTransaction =
        http.get("/transactions/$transactionId") {
            parameter("include_raw", includeRaw)
            parameter("include_proof", includeProof)
        }

    public override suspend fun createTransaction(
        blockchainId: String,
        hashAsHex: String,
        tx: ByteArray
    ): Unit =
        http.post("/transactions") {
            parameter("blockchain_id", blockchainId)
            parameter("transaction_id", hashAsHex)
            parameter("data", tx.encodeBase64())
        }

    public override suspend fun getBlocks(
        blockchainId: String,
        includeRaw: Boolean,
        includeTx: Boolean,
        includeTxRaw: Boolean,
        includeTxProof: Boolean,
        beginBlockNumber: ULong?,
        endBlockNumber: ULong?,
        maxPageSize: Int?
    ): BdbBlockList =
        http.get("/blocks") {
            parameter("blockchain_id", blockchainId)
            parameter("include_raw", includeRaw)
            parameter("include_tx", includeTx)
            parameter("include_tx_raw", includeTxRaw)
            parameter("include_tx_proof", includeTxProof)
            parameter("start_height", beginBlockNumber)
            parameter("end_height", endBlockNumber)
            parameter("max_page_size", maxPageSize)
        }

    public override suspend fun getBlock(
        blockId: String,
        includeTx: Boolean,
        includeTxRaw: Boolean,
        includeTxProof: Boolean
    ): BdbBlock =
        http.get("/blocks/$blockId") {
            parameter("include_raw", false)
            parameter("include_tx", includeTx)
            parameter("include_tx_raw", includeTxRaw)
            parameter("include_tx_proof", includeTxProof)
        }

    public override suspend fun getBlockWithRaw(blockId: String): BdbBlock =
        http.get("/blocks/$blockId") {
            parameter("include_raw", true)
            parameter("include_tx", false)
            parameter("include_tx_raw", false)
            parameter("include_tx_proof", false)
        }


    public override suspend fun addressLookup(
        domainName: String,
        vararg currencyCodes: String
    ): BdbAddressesResult = addressLookup(domainName, currencyCodes.toList())

    public override suspend fun addressLookup(
        domainName: String,
        currencyCodeList: List<String>
    ): BdbAddressesResult {
        return try {
            http.get<BdbAddresses>("/resolve") {
                parameter("domain_name", domainName)
                currencyCodeList.forEach {
                    parameter("currency_code", it)
                }
            }
        } catch (e: Throwable) {
            val response = (e as? ResponseException)?.response
            BdbAddressesError(
                status = response?.status?.value ?: -1,
                body = response?.readText(),
            )
        }
    }
}

public suspend fun createUserToken(
    http: HttpClient,
    clientToken: String,
    deviceId: String,
    pubKey: String,
    signature: String
): BdbUserTokenResult {
    return try {
        http.post<BdbUserTokenResult.Success>("/users/token") {
            header("Authorization", "Bearer $clientToken")
            contentType(ContentType.Application.Json)
            body = buildJsonObject {
                put("device_id", deviceId)
                put("pub_key", pubKey)
                put("signature", signature)
            }
        }
    } catch (e: Throwable) {
        val response = (e as? ResponseException)?.response
        BdbUserTokenResult.Error(
            status = response?.status?.value ?: -1,
            body = response?.readText(),
        )
    }
}
