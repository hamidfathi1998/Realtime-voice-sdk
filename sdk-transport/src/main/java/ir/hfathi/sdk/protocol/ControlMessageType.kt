package ir.hfathi.sdk.protocol

enum class ControlMessageType(val code: Int) {
    START_SESSION(1),
    END_SESSION(2),
    SET_CODEC(3),
    SET_SAMPLE_RATE(4),
    PAUSE(5),
    RESUME(6),
    MUTE(7),
    UNMUTE(8),
    REQUEST_STATS(9)
}