package com.yunx.app.ui.jcef

import org.junit.Assert.*
import org.junit.Test

class JcefExitPolicyTest {
    @Test fun `Mac exit saves browser data and never invokes the crashing native destructor`() {
        val calls = mutableListOf<String>()
        assertTrue(JcefExitPolicy.finish(true, { calls += "save"; true }, { error("unsafe Mac disposal") }))
        assertEquals(listOf("save"), calls)
    }
    @Test fun `failed persistence keeps the application alive and does not dispose the browser`() {
        var disposed = false
        assertFalse(JcefExitPolicy.finish(true, { false }, { disposed = true; true }))
        assertFalse(disposed)
    }
    @Test fun `other platforms save data before waiting for native shutdown and preserve failure`() {
        val calls = mutableListOf<String>()
        assertFalse(JcefExitPolicy.finish(false, { calls += "save"; true }, { calls += "dispose"; false }))
        assertEquals(listOf("save", "dispose"), calls)
    }
}
