package ir.hfathi.sdk.core

import ir.hfathi.sdk.core.models.AudioFrame
import ir.hfathi.sdk.core.models.ClientEvent
import ir.hfathi.sdk.core.models.SessionState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Public host-facing contract of the Realtime Voice SDK.
 * Implementations arrive in later chapters (transport + audio + orchestration).
 */
interface RealtimeVoiceClient {
    /** Current session lifecycle state. Always has a value (starts at Idle). */
    val state: StateFlow<SessionState>

    /** Hot stream of session events. Events emitted with no collector are not replayed. */
    val events: SharedFlow<ClientEvent>

    suspend fun startSession()
    suspend fun stopSession()
    suspend fun sendAudio(frame: AudioFrame)
    suspend fun interrupt()
}