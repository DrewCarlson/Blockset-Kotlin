package cli

import com.github.ajalt.mordant.terminal.Terminal
import drewcarlson.blockset.BdbService
import io.ktor.client.*
import io.ktor.client.features.logging.*
import io.ktor.utils.io.core.*
import kotlinx.cli.*
import kotlinx.serialization.json.Json
import kotlin.native.concurrent.SharedImmutable

/** Open a URL with the default browser */
expect fun openUrl(url: String)
/** Get an environment variable by name */
expect fun getEnv(key: String): String?

/** Pretty JSON formatter for console output. */
@SharedImmutable
val prettyJson = Json {
    isLenient = true
    prettyPrint = true
}

fun runCli(args: Array<String>) {
    val terminal = Terminal()
    val token = checkNotNull(getEnv("BDB_CLIENT_TOKEN"))
    HttpClient {
        install(Logging) {
            level = LogLevel.INFO
            logger = Logger.SIMPLE
        }
    }.use { httpClient ->
        val blockset = BdbService.createForTest(token, httpClient)
        with(ArgParser("blockset")) {
            subcommands(
                AuthCommand(blockset, terminal),
                BlocksCommand(blockset),
                TransfersCommand(blockset),
                CurrenciesCommand(blockset),
                BlockchainsCommand(blockset, terminal),
                TransactionsCommand(blockset),
                PushEndpointsCommand(blockset),
                SubscriptionsCommand(blockset)
            )
            parse(args) // Parse and run
        }
    }
}
