package cli

import com.github.ajalt.clikt.core.*
import drewcarlson.blockset.BdbService

class PushEndpointsCommand(
    private val blockset: BdbService
) : CliktCommand(
    name = "push",
    help = "" // TODO
) {

    override fun run() {
        TODO("not implemented")
    }
}
