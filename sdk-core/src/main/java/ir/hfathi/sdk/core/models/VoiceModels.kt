package ir.hfathi.sdk.core.models

/**
 * Represents the immutable states of a Realtime Voice Session.
 */
sealed interface SessionState {
    data object Idle : SessionState
    data object Connecting : SessionState
    data object Connected : SessionState
    data object Listening : SessionState
    data object Speaking : SessionState
    data object Disconnecting : SessionState
    data class Failed(val error: VoiceError) : SessionState
}

/**
 * Represents incoming and outgoing events during an active voice session.
 */
sealed interface ClientEvent {
    data object UserStartedSpeaking : ClientEvent
    data object UserStoppedSpeaking : ClientEvent
    data class ServerAudioChunk(val frame: AudioFrame) : ClientEvent
    data class TranscriptReceived(val text: String, val isFinal: Boolean, val isUser: Boolean) : ClientEvent
    data object Interrupted : ClientEvent
    data object Disconnected : ClientEvent
}

/**
 * Standard error taxonomy for Voice SDK operations.
 */
sealed class VoiceError(
    override val message: String,
    override val cause: Throwable? = null
) : Exception(message, cause) {
    class Network(message: String, cause: Throwable? = null) : VoiceError(message, cause)
    class Authentication(message: String, cause: Throwable? = null) : VoiceError(message, cause)
    class AudioHardware(message: String, cause: Throwable? = null) : VoiceError(message, cause)
    class Protocol(message: String, cause: Throwable? = null) : VoiceError(message, cause)
    class Unknown(message: String, cause: Throwable? = null) : VoiceError(message, cause)
}

/**
 * Raw PCM audio frame with metadata.
 * equals/hashCode are overridden because ByteArray uses reference equality by default.
 */
data class AudioFrame(
    val data: ByteArray,
    val timestampNs: Long = System.nanoTime(),
    val sampleRate: Int = 24000,
    val channels: Int = 1
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AudioFrame
        if (!data.contentEquals(other.data)) return false
        if (timestampNs != other.timestampNs) return false
        if (sampleRate != other.sampleRate) return false
        if (channels != other.channels) return false
        return true
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + timestampNs.hashCode()
        result = 31 * result + sampleRate
        result = 31 * result + channels
        return result
    }
}