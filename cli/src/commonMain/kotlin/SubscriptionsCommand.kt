package cli

import com.github.ajalt.clikt.core.*
import drewcarlson.blockset.BdbService

class SubscriptionsCommand(
    private val blockset: BdbService
) : CliktCommand(
    name = "subscriptions",
    help = "" // TODO
) {

    override fun run() {
        TODO("not implemented")
    }
}
