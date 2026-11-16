package com.yunx.app.data.download

/** Separate a stalled/outlier connection from a source that is slow on every connection. */
internal object SlowConnectionPolicy {
    fun shouldReplace(
        currentBps: Long,
        idleMs: Long,
        idleThresholdMs: Long,
        peerSpeeds: Collection<Long>
    ): Boolean {
        if (idleMs >= idleThresholdMs.coerceAtLeast(1L)) return true
        val movingPeers = peerSpeeds.filter { it > 0L }.sorted()
        if (movingPeers.isEmpty()) return false
        // Use the lower median so one unusually fast peer cannot trigger mass reconnects.
        val median = movingPeers[(movingPeers.size - 1) / 2]
        return currentBps.coerceAtLeast(0L) < median / 2
    }
}
