package cli

import platform.AppKit.NSWorkspace
import platform.Foundation.NSProcessInfo
import platform.Foundation.NSURL

actual fun openUrl(url: String) {
    val nsUrl = checkNotNull(NSURL.URLWithString(url))
    NSWorkspace.sharedWorkspace.openURL(nsUrl)
}

actual fun getEnv(key: String): String? =
    NSProcessInfo.processInfo.environment[key] as? String
