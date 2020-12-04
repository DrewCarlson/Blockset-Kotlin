package cli

import drewcarlson.blockset.BdbService
import kotlinx.cli.Subcommand

class PushEndpointsCommand(
    private val blockset: BdbService
) : Subcommand(
    name = "push",
    actionDescription = "" // TODO
) {

    override fun execute() {
        TODO("not implemented")
    }
}
