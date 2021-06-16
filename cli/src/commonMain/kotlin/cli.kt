package cli

import com.github.ajalt.clikt.core.*
import com.github.ajalt.mordant.terminal.Terminal
import drewcarlson.blockset.BdbService
import io.ktor.client.*
import io.ktor.client.features.logging.*
import io.ktor.utils.io.core.*
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
    useAlternativeNames = false
}

class BlocksetCommand : CliktCommand(printHelpOnEmptyArgs = true) {
    override fun run() = Unit
}

fun runCli(args: Array<String>) {
    val terminal = Terminal().apply {
        info.updateTerminalSize()
    }
    val token = checkNotNull(getEnv("BDB_CLIENT_TOKEN"))
    HttpClient {
        install(Logging) {
            level = LogLevel.INFO
            logger = Logger.SIMPLE
        }
    }.use { httpClient ->
        val blockset = BdbService.createForTest(token, httpClient)

        BlocksetCommand()
            .subcommands(
                AuthCommand(blockset, terminal),
                BlocksCommand(blockset, terminal),
                TransfersCommand(blockset),
                CurrenciesCommand(blockset),
                BlockchainsCommand(blockset, terminal),
                TransactionsCommand(blockset),
                PushEndpointsCommand(blockset),
                SubscriptionsCommand(blockset)
            ).main(args)
    }
}
