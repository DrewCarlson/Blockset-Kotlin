package cli

import drewcarlson.blockset.BdbService
import kotlinx.cli.Subcommand

class BlocksCommand(
    private val blockset: BdbService
) : Subcommand(
    name = "blocks",
    actionDescription = "" // TODO
) {

    override fun execute() {
        TODO("not implemented")
    }
}
