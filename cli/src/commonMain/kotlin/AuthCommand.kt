package cli

import drewcarlson.blockset.BdbService
import kotlinx.cli.Subcommand

class AuthCommand(
    private val blockset: BdbService
) : Subcommand(
    name = "auth",
    actionDescription = "" // TODO
) {

    override fun execute() {
        openUrl("https://blockset.com")
    }
}
