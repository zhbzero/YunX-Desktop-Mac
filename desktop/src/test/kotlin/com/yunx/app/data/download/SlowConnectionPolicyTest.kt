package com.yunx.app.data.download

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SlowConnectionPolicyTest {
    private fun replace(speed: Long, idle: Long, peers: List<Long>) =
        SlowConnectionPolicy.shouldReplace(speed, idle, 15_000L, peers)

    @Test fun `uniformly throttled connections keep their progress without reconnecting`() {
        assertFalse(replace(8 * 1024, 0, List(31) { 10 * 1024L }))
        assertFalse(replace(10 * 1024, 0, List(31) { 8 * 1024L }))
    }

    @Test fun `one fast connection does not cause all other connections to be replaced`() {
        assertFalse(replace(8 * 1024, 0, List(30) { 10 * 1024L } + 1024 * 1024L))
    }

    @Test fun `a genuine slow outlier can be replaced`() {
        assertTrue(replace(1024, 0, listOf(80 * 1024L, 100 * 1024L, 120 * 1024L)))
    }

    @Test fun `stalled connections are replaced after the idle threshold`() {
        assertTrue(replace(0, 15_000, listOf(0, 0)))
        assertFalse(replace(0, 5_000, listOf(0, 0)))
    }

    @Test fun `a single active slow connection stays alive but a stalled one can recover`() {
        assertFalse(replace(8 * 1024, 0, emptyList()))
        assertTrue(replace(0, 15_000, emptyList()))
    }
}
