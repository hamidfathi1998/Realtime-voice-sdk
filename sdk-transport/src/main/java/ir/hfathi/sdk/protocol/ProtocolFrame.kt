package ir.hfathi.sdk.protocol

data class ProtocolFrame(
    val frameType: FrameType,
    val sequenceNumber: UInt,
    val timestamp: Long,
    val payload: ByteArray,
    val checksum: UInt = calculateChecksum(payload)
) {
    fun toBytes(): ByteArray = TODO()
    companion object {
        fun fromBytes(data: ByteArray): Result<ProtocolFrame> = TODO()
    }
}