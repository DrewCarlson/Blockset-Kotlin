package cli

import drewcarlson.blockset.BdbService
import kotlinx.cli.ArgType
import kotlinx.cli.Subcommand
import kotlinx.cli.default
import kotlinx.cli.optional
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString


class BlockchainsCommand(
    private val blockset: BdbService
) : Subcommand(
    name = "blockchains",
    actionDescription = "Retrieves supported blockchains."
) {
    private val blockchainId by argument(
        ArgType.String,
        fullName = "blockchainId",
        description = "" // TODO
    ).optional()

    private val testnet by option(
        ArgType.Boolean,
        fullName = "testnet",
        description = "" // TODO
    ).default(false)

    override fun execute(): Unit = runBlocking {
        val blockchains = blockchainId?.let { listOf(blockset.getBlockchain(it)) }
            ?: blockset.getBlockchains(testnet = testnet)
                .embedded.blockchains

        blockchains.forEach { blockchain ->
            println(prettyJson.encodeToString(blockchain))
        }
    }
}
