package cli

import kotlinx.cinterop.autoreleasepool
import kotlin.native.concurrent.freeze
import kotlin.system.exitProcess

fun main(args: Array<String>): Unit = autoreleasepool {
    Platform.isMemoryLeakCheckerActive = false // TODO: Fix worker leak, ktor maybe?
    setUnhandledExceptionHook({ error: Throwable ->
        println("Unhandled Exception: $error")
        error.printStackTrace()
        exitProcess(-1)
    }.freeze())

    runCli(args)
}
