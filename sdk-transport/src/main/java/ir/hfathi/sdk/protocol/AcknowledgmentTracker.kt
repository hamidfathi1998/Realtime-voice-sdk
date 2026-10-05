package ir.hfathi.sdk.protocol

interface AcknowledgmentTracker {
    fun trackFrame(frameId: UInt): Unit
    fun acknowledge(frameId: UInt): Result<Unit>
    fun isAcknowledged(frameId: UInt): Boolean
    fun getPendingFrames(): List<UInt>
}