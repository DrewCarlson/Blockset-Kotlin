package cli

import drewcarlson.blockset.BdbService
import kotlinx.cli.Subcommand

class TransactionsCommand(
    private val blockset: BdbService
) : Subcommand(
    name = "transactions",
    actionDescription = "" // TODO
) {

    override fun execute() {
        TODO("not implemented")
    }
}
