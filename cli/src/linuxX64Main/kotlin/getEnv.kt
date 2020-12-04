package cli

import kotlinx.cinterop.toKStringFromUtf8
import platform.posix.getenv

actual fun getEnv(key: String): String? {
    return getenv(key)?.toKStringFromUtf8()
}
