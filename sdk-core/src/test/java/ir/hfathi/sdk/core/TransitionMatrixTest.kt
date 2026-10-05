package ir.hfathi.sdk.core

import ir.hfathi.sdk.core.models.SessionState
import ir.hfathi.sdk.core.models.VoiceError
import ir.hfathi.sdk.core.statemachine.SessionStateMachine
import kotlin.test.Test
import kotlin.test.assertEquals

class TransitionMatrixTest {

    private val error = VoiceError.Unknown("boom")

    private val allStates: List<SessionState> = listOf(
        SessionState.Idle,
        SessionState.Connecting,
        SessionState.Connected,
        SessionState.Listening,
        SessionState.Speaking,
        SessionState.Disconnecting,
        SessionState.Failed(error),
    )

    /** Source of truth, mirrors the KDoc table in SessionStateMachine. */
    private val allowed: Map<String, Set<String>> = mapOf(
        "Idle" to setOf("Connecting"),
        "Connecting" to setOf("Connected", "Failed", "Disconnecting"),
        "Connected" to setOf("Listening", "Speaking", "Disconnecting", "Failed"),
        "Listening" to setOf("Speaking", "Connected", "Disconnecting", "Failed"),
        "Speaking" to setOf("Listening", "Connected", "Disconnecting", "Failed"),
        "Disconnecting" to setOf("Idle", "Failed"),
        "Failed" to setOf("Idle", "Connecting"),
    )

    private fun nameOf(state: SessionState): String = state::class.simpleName!!

    /** Drives a fresh machine into [target] using only legal transitions. */
    private fun machineIn(target: SessionState): SessionStateMachine {
        val sm = SessionStateMachine()
        val path: List<SessionState> = when (target) {
            is SessionState.Idle -> emptyList()
            is SessionState.Connecting -> listOf(SessionState.Connecting)
            is SessionState.Connected -> listOf(SessionState.Connecting, SessionState.Connected)
            is SessionState.Listening -> listOf(SessionState.Connecting, SessionState.Connected, SessionState.Listening)
            is SessionState.Speaking -> listOf(SessionState.Connecting, SessionState.Connected, SessionState.Speaking)
            is SessionState.Disconnecting -> listOf(SessionState.Connecting, SessionState.Disconnecting)
            is SessionState.Failed -> {
                sm.fail(target.error)
                return sm
            }
        }
        path.forEach { step -> check(sm.transitionTo(step)) { "setup failed at $step" } }
        assertEquals(target, sm.state.value)
        return sm
    }

    @Test
    fun `every from-to pair follows the transition table`() {
        for (from in allStates) {
            for (to in allStates) {
                val sm = machineIn(from)
                val expected = from == to || nameOf(to) in allowed.getValue(nameOf(from))

                val result = sm.transitionTo(to)

                assertEquals(expected, result, "transition ${nameOf(from)} -> ${nameOf(to)}")
                assertEquals(if (expected) to else from, sm.state.value, "state after ${nameOf(from)} -> ${nameOf(to)}")
            }
        }
    }

    @Test
    fun `transition table covers every state`() {
        assertEquals(allStates.map(::nameOf).toSet(), allowed.keys)
    }
}