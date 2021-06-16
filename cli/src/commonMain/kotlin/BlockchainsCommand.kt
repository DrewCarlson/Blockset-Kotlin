package cli

import com.github.ajalt.clikt.core.*
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import drewcarlson.blockset.BdbService
import kotlinx.coroutines.runBlocking


class BlockchainsCommand(
    private val blockset: BdbService,
    private val terminal: Terminal
) : CliktCommand(
    name = "blockchains",
    help = "Retrieves supported blockchains."
) {
    private val blockchainId by option(
        help = "A blockchain network id to query."
    )

    private val testnet by option(
        help = "When true, only display test network blockchains."
    ).flag(default = false)

    private val fees by option(
        help = "When true, prints current network fee data."
    ).flag(default = false)

    override fun run(): Unit = runBlocking {
        val blockchains = blockchainId?.let { listOf(blockset.getBlockchain(it)) }
            ?: blockset.getBlockchains(testnet = testnet)
                .embedded.blockchains

        terminal.println(table {
            header {
                row("Name", "ID", "Height", "Network") {
                    if (fees) {
                        cell("Fees")
                    }
                }
            }
            body {
                blockchains.forEach { blockchain ->
                    with(blockchain) {
                        row(name, id, blockHeight, network) {
                            if (fees) {
                                cell(
                                    feeEstimates.joinToString { feeEstimate ->
                                        "${feeEstimate.tier}:${feeEstimate.fee.value}"
                                    }
                                )
                            }
                        }
                    }
                }
            }
        })
    }
}
