package com.yunx.app.util

/** Platform decisions shared by native integrations and engine provisioning. */
object DesktopPlatform {
    private val os = System.getProperty("os.name", "").lowercase()
    val isWindows = os.startsWith("windows")
    val isMac = os.contains("mac") || os.contains("darwin")
    val executableSuffix = if (isWindows) ".exe" else ""
    val gopeedOs = when {
        isMac -> "macos"
        isWindows -> "windows"
        else -> "linux"
    }
    val arch = when (System.getProperty("os.arch", "").lowercase()) {
        "arm64", "aarch64" -> "arm64"
        else -> "amd64"
    }
}
