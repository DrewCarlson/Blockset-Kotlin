package cli

import drewcarlson.blockset.BdbService
import io.ktor.client.*
import io.ktor.client.features.logging.*
import io.ktor.utils.io.core.*
import kotlinx.cli.*
import kotlinx.serialization.json.Json
import kotlin.native.concurrent.SharedImmutable

expect fun getEnv(key: String): String?

@SharedImmutable
val prettyJson = Json {
    isLenient = true
    prettyPrint = true
}

fun runCli(args: Array<String>) {
    val token = checkNotNull(getEnv("BDB_CLIENT_TOKEN"))
    HttpClient {
        install(Logging) {
            level = LogLevel.INFO
            logger = Logger.SIMPLE
        }
    }.use { httpClient ->
        val blockset = BdbService.createForTest(token, httpClient)
        val argParser = ArgParser("blockset")
        argParser.subcommands(
            BlocksCommand(blockset),
            TransfersCommand(blockset),
            CurrenciesCommand(blockset),
            BlockchainsCommand(blockset),
            TransactionsCommand(blockset),
            PushEndpointsCommand(blockset),
            SubscriptionsCommand(blockset)
        )
        argParser.parse(args)
    }
}
