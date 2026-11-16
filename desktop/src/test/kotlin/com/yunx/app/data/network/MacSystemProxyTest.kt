package com.yunx.app.data.network

import org.junit.Assert.assertEquals
import org.junit.Test

class MacSystemProxyTest {
    @Test fun `reads enabled HTTPS proxy from scutil output`() {
        assertEquals(SystemProxy.Inspect.Proxy("127.0.0.1", 7890), SystemProxy.parseMacProxy("""
            <dictionary> {
              HTTPEnable : 0
              HTTPSEnable : 1
              HTTPSProxy : 127.0.0.1
              HTTPSPort : 7890
            }
        """.trimIndent()))
    }

    @Test fun `PAC is reported explicitly instead of pretending it is a static proxy`() {
        assertEquals(SystemProxy.Inspect.PacUnsupported, SystemProxy.parseMacProxy("""
            ProxyAutoConfigEnable : 1
            HTTPEnable : 1
            HTTPProxy : localhost
            HTTPPort : 8080
        """.trimIndent()))
    }

    @Test fun `disabled or invalid proxy falls back to direct`() {
        assertEquals(SystemProxy.Inspect.Direct, SystemProxy.parseMacProxy("""
            HTTPEnable : 1
            HTTPProxy : localhost
            HTTPPort : 70000
        """.trimIndent()))
        assertEquals(SystemProxy.Inspect.Direct, SystemProxy.parseMacProxy("HTTPEnable : 0"))
    }
}
