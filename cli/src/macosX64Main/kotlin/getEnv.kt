package cli

import platform.Foundation.NSProcessInfo

actual fun getEnv(key: String): String? =
    NSProcessInfo.processInfo.environment[key] as? String
