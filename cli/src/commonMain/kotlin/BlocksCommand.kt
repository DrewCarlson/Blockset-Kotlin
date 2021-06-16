package cli

import com.github.ajalt.clikt.core.*
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.mordant.terminal.Terminal
import drewcarlson.blockset.BdbService
import kotlinx.coroutines.runBlocking

class BlocksCommand(
    private val blockset: BdbService,
    private val terminal: Terminal
) : CliktCommand(
    name = "blocks",
    help = "" // TODO
) {
    private val blockchainId by option(
        help = "A blockchain network id to query."
    )

    private val endHeight by option(
        help = ""
    )

    override fun run() = runBlocking {
        TODO("not implemented")
    }
}
