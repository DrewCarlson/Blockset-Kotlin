package cli

import drewcarlson.blockset.BdbService
import kotlinx.cli.Subcommand

class CurrenciesCommand(
    private val blockset: BdbService
) : Subcommand(
    name = "currencies",
    actionDescription = "" // TODO
) {

    override fun execute() {
        TODO("not implemented")
    }
}
