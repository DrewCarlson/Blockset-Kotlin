package drewcarlson.blockset

import kotlinx.coroutines.*

actual fun runBlocking(block: suspend CoroutineScope.() -> Unit) =
    kotlinx.coroutines.runBlocking(block = block)

