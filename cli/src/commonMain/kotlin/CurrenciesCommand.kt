package cli

import com.github.ajalt.clikt.core.*
import drewcarlson.blockset.BdbService

class CurrenciesCommand(
    private val blockset: BdbService
) : CliktCommand(
    name = "currencies",
    help = "" // TODO
) {

    override fun run() {
        TODO("not implemented")
    }
}
