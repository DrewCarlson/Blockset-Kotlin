package cli

import drewcarlson.blockset.BdbService
import kotlinx.cli.Subcommand

class SubscriptionsCommand(
    private val blockset: BdbService
) : Subcommand(
    name = "subscriptions",
    actionDescription = "" // TODO
) {

    override fun execute() {
        TODO("not implemented")
    }
}
