package cli

import com.github.ajalt.clikt.core.*
import com.github.ajalt.mordant.terminal.Terminal
import drewcarlson.blockset.BdbService
import kotlinx.coroutines.runBlocking

class AuthCommand(
    private val blockset: BdbService,
    private val terminal: Terminal
) : CliktCommand(
    name = "auth",
    help = "Manage API authentication"
) {

    override fun run() = runBlocking {
        val confirmed = confirm("Blockset will be opened in your browser, login or signup to receive a client token.")
        if (confirmed == true) {
            terminal.println("Opening https://blockset.com")
            openUrl("https://blockset.com")
        } else {
            terminal.println("Cancelled")
        }
    }
}
