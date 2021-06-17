package app

import drewcarlson.blockset.*
import drewcarlson.blockset.model.*
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.*
import kotlin.system.*

val bdbToken = ""
val etherscanApiKey = ""
val ethAddress = "".lowercase()

private val json = Json {
    ignoreUnknownKeys = true
}

val blockset = BdbService.createForTest(bdbToken)
val http = HttpClient {
    Json {
        serializer = KotlinxSerializer(json)
    }
}

suspend fun main() {
    val normalTxns = etherscanTxns("txlist")
    val internalTxns = etherscanTxns("txlistinternal")
    val tokenTxns = etherscanTxns("tokentx")
    val token721Txns = etherscanTxns("tokennfttx")
    val etherscanTxns = normalTxns + internalTxns + tokenTxns + token721Txns
    val etherscanHashes = etherscanTxns.map {
        it.jsonObject.getValue("hash").jsonPrimitive.content.lowercase()
    }.distinct()

    val bdbTxns = bdbTxns()
    val bdbHashes = bdbTxns.map { it.hash.lowercase() }.distinct()

    println("Unmatched hashes: ${etherscanHashes - bdbHashes}")
    println("Etherscan count: ${etherscanHashes.size}")
    println("Blockset count: ${bdbHashes.size}")
    exitProcess(0)
}


private suspend fun etherscanTxns(action: String): JsonArray {
    return http.get<JsonElement> {
        url {
            protocol = URLProtocol.HTTPS
            host = "api.etherscan.io"
            path("api")
            parameter("module", "account")
            parameter("action", action)
            parameter("address", ethAddress)
            parameter("apiKey", etherscanApiKey)
            parameter("startblock", 0)
            parameter("endblock", 99999999)
            parameter("sort", "asc")
        }
    }.jsonObject.getValue("result").jsonArray
}

private suspend fun bdbTxns(): List<BdbTransaction> {
    suspend fun makeReq(startBlock: ULong) = blockset.getTransactions(
        "ethereum-mainnet",
        listOf(ethAddress),
        beginBlockNumber = startBlock,
        mergeCurrencies = true,
        includeTransfers = true,
        maxPageSize = 1000,
    )
    val result = makeReq(0u)
    val txns = result.embedded.transactions.toMutableList()
    var nextLink = result.links?.next?.href
    while (nextLink != null) {
        val startBlock = Url(nextLink).parameters["start_height"]!!.toULong()
        val nextResult = makeReq(startBlock)
        txns += nextResult.embedded.transactions
        nextLink = nextResult.links?.next?.href
    }
    return txns.toList()
}
