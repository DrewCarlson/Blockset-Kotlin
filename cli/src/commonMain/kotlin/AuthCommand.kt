package cli

import com.github.ajalt.mordant.terminal.Terminal
import drewcarlson.blockset.BdbService
import kotlinx.cli.Subcommand

class AuthCommand(
    private val blockset: BdbService,
    private val terminal: Terminal
) : Subcommand(
    name = "auth",
    actionDescription = "" // TODO
) {

    override fun execute() {
        openUrl("https://blockset.com")
    }
}
