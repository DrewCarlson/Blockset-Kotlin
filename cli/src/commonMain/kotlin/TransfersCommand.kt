package cli

import com.github.ajalt.clikt.core.*
import drewcarlson.blockset.BdbService

class TransfersCommand(
    private val blockset: BdbService
) : CliktCommand(
    name = "transfers",
    help = "" // TODO
) {

    override fun run() {
        TODO("not implemented")
    }
}
