package com.yunx.app.data.update

import com.yunx.app.util.DesktopPlatform
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MacReleaseAssetTest {
    @Test fun `only offers a DMG for the matching Mac architecture`() {
        val arch = DesktopPlatform.arch
        assertTrue(UpdateChecker.isCompatibleMacAsset("YunX-Desktop-1.3.4-macos-$arch.dmg"))
        assertTrue(UpdateChecker.isCompatibleMacAsset("YunX-Desktop-universal.dmg"))
        val other = if (arch == "arm64") "amd64" else "arm64"
        assertFalse(UpdateChecker.isCompatibleMacAsset("YunX-Desktop-$other.dmg"))
        assertFalse(UpdateChecker.isCompatibleMacAsset("YunX-Desktop-setup.exe"))
        assertFalse(UpdateChecker.isCompatibleMacAsset("YunX-Desktop-windows.zip"))
    }
}
