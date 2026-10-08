package ir.hfathi.sdk.protocol

sealed class ControlMessage {
    data class StartSession(val sessionId: String, val userId: String) : ControlMessage()
    data class EndSession(val sessionId: String, val reason: String) : ControlMessage()
    data class SetCodec(val codec: String) : ControlMessage()
    data class SetSampleRate(val sampleRate: Int) : ControlMessage()
    data class Pause(val sessionId: String) : ControlMessage()
    data class Resume(val sessionId: String) : ControlMessage()
    data class Mute(val sessionId: String) : ControlMessage()
    data class Unmute(val sessionId: String) : ControlMessage()
    data class RequestStats(val sessionId: String) : ControlMessage()
}