package ir.hfathi.sdk.protocol

data class AudioFrame(
    val samples: ShortArray,
    val sampleRate: Int = 16000,
    val timestamp: Long,
    val sequenceNumber: UInt
) {
    init {
        require(samples.size in listOf(80, 160, 320, 960)) {
            "Audio frame size must be 80, 160, 320, or 960 samples"
        }
    }
}