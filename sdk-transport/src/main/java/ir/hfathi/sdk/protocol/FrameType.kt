package ir.hfathi.sdk.protocol

enum class FrameType(val id: Byte) {
    AUDIO_DATA(0x01),
    CONTROL(0x02),
    HEARTBEAT(0x03),
    ACK(0x04),
    ERROR(0x05)
}