package ir.hfathi.sdk.transport

interface LatencyOptimizer {
    fun measureRoundTripTime(): Long
    fun measureJitter(): Long
    fun adjustBufferSize(newSize: Int): Unit
    fun suggestFrameDropping(): Boolean
}