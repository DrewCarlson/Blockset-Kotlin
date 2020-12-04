package cli

import kotlinx.cinterop.toKStringFromUtf8
import platform.posix.getenv
import platform.posix.system

actual fun openUrl(url: String) {
    system("xdg-open $url")
}

actual fun getEnv(key: String): String? {
    return getenv(key)?.toKStringFromUtf8()
}
