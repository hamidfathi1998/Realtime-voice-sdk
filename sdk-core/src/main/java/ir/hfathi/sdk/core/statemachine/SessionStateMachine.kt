package ir.hfathi.sdk.core.statemachine

import ir.hfathi.sdk.core.models.ClientEvent
import ir.hfathi.sdk.core.models.SessionState
import ir.hfathi.sdk.core.models.VoiceError
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Thread-safe deterministic state machine managing the voice session lifecycle and transitions.
 *
 * Transition table:
 *   Idle          -> Connecting
 *   Connecting    -> Connected | Failed | Disconnecting
 *   Connected     -> Listening | Speaking | Disconnecting | Failed
 *   Listening     -> Speaking | Connected | Disconnecting | Failed
 *   Speaking      -> Listening | Connected | Disconnecting | Failed
 *   Disconnecting -> Idle | Failed
 *   Failed        -> Idle | Connecting
 * Same-state transitions are accepted as no-ops.
 * fail() and reset() are escape hatches that bypass the table.
 */
class SessionStateMachine {
    private val _state = MutableStateFlow<SessionState>(SessionState.Idle)
    val state: StateFlow<SessionState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<ClientEvent>(extraBufferCapacity = 64)
    val events: SharedFlow<ClientEvent> = _events.asSharedFlow()

    @Synchronized
    fun transitionTo(newState: SessionState): Boolean {
        val current = _state.value
        if (isValidTransition(current, newState)) {
            _state.value = newState
            return true
        }
        return false
    }

    @Synchronized
    fun emitEvent(event: ClientEvent) {
        _events.tryEmit(event)
    }

    @Synchronized
    fun fail(error: VoiceError) {
        _state.value = SessionState.Failed(error)
    }

    @Synchronized
    fun reset() {
        _state.value = SessionState.Idle
    }

    private fun isValidTransition(from: SessionState, to: SessionState): Boolean {
        if (from == to) return true
        return when (from) {
            is SessionState.Idle -> to is SessionState.Connecting
            is SessionState.Connecting -> to is SessionState.Connected || to is SessionState.Failed || to is SessionState.Disconnecting
            is SessionState.Connected -> to is SessionState.Listening || to is SessionState.Speaking || to is SessionState.Disconnecting || to is SessionState.Failed
            is SessionState.Listening -> to is SessionState.Speaking || to is SessionState.Connected || to is SessionState.Disconnecting || to is SessionState.Failed
            is SessionState.Speaking -> to is SessionState.Listening || to is SessionState.Connected || to is SessionState.Disconnecting || to is SessionState.Failed
            is SessionState.Disconnecting -> to is SessionState.Idle || to is SessionState.Failed
            is SessionState.Failed -> to is SessionState.Idle || to is SessionState.Connecting
        }
    }
}