package cli

import drewcarlson.blockset.BdbService
import kotlinx.cli.Subcommand

class TransfersCommand(
    private val blockset: BdbService
) : Subcommand(
    name = "transfers",
    actionDescription = "" // TODO
) {

    override fun execute() {
        TODO("not implemented")
    }
}
