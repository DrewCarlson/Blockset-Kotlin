package cli

import kotlin.native.concurrent.freeze
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    setUnhandledExceptionHook({ error: Throwable ->
        println("Unhandled Exception: $error")
        error.printStackTrace()
        exitProcess(-1)
    }.freeze())

    runCli(args)
}
