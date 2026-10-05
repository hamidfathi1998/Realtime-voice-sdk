package ir.hfathi.sdk.core

import app.cash.turbine.test
import ir.hfathi.sdk.core.models.ClientEvent
import ir.hfathi.sdk.core.models.SessionState
import ir.hfathi.sdk.core.models.VoiceError
import ir.hfathi.sdk.core.statemachine.SessionStateMachine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class StateMachineEdgeCasesTest {

    @Test
    fun `same-state transition is accepted as a no-op`() {
        val sm = SessionStateMachine()
        assertTrue(sm.transitionTo(SessionState.Idle))
        assertEquals(SessionState.Idle, sm.state.value)
    }

    @Test
    fun `fail() works from any state, even Idle`() {
        val sm = SessionStateMachine()
        sm.fail(VoiceError.AudioHardware("mic busy"))
        assertIs<SessionState.Failed>(sm.state.value)
    }

    @Test
    fun `a new Failed cannot replace Failed via transitionTo but fail() can`() {
        val sm = SessionStateMachine()
        val first = VoiceError.Network("first")
        val second = VoiceError.Protocol("second")
        sm.fail(first)

        assertFalse(sm.transitionTo(SessionState.Failed(second)))
        assertEquals(first, (sm.state.value as SessionState.Failed).error)

        sm.fail(second)
        assertEquals(second, (sm.state.value as SessionState.Failed).error)
    }

    @Test
    fun `reset() returns to Idle from any state`() {
        val sm = SessionStateMachine()
        sm.transitionTo(SessionState.Connecting)
        sm.transitionTo(SessionState.Connected)
        sm.transitionTo(SessionState.Speaking)
        sm.reset()
        assertEquals(SessionState.Idle, sm.state.value)
    }

    @Test
    fun `recovery from Failed allows reconnecting`() {
        val sm = SessionStateMachine()
        sm.fail(VoiceError.Network("drop"))
        assertTrue(sm.transitionTo(SessionState.Connecting))
        assertTrue(sm.transitionTo(SessionState.Connected))
    }

    @Test
    fun `StateFlow does not re-emit when the same state is set again`() = runTest {
        val sm = SessionStateMachine()
        sm.state.test {
            assertEquals(SessionState.Idle, awaitItem())
            sm.transitionTo(SessionState.Idle)
            expectNoEvents()
        }
    }

    @Test
    fun `events emitted before anyone collects are dropped (no replay)`() = runTest {
        val sm = SessionStateMachine()
        sm.emitEvent(ClientEvent.UserStartedSpeaking) // nobody listening
        sm.events.test {
            sm.emitEvent(ClientEvent.UserStoppedSpeaking)
            assertEquals(ClientEvent.UserStoppedSpeaking, awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun `transcript events keep their flags`() = runTest {
        val sm = SessionStateMachine()
        sm.events.test {
            sm.emitEvent(ClientEvent.TranscriptReceived("hi", isFinal = false, isUser = true))
            val event = assertIs<ClientEvent.TranscriptReceived>(awaitItem())
            assertEquals("hi", event.text)
            assertFalse(event.isFinal)
            assertTrue(event.isUser)
        }
    }

    @Test
    fun `concurrent transitions never leave an illegal state`() = runTest {
        val sm = SessionStateMachine()
        sm.transitionTo(SessionState.Connecting)
        sm.transitionTo(SessionState.Connected)

        withContext(Dispatchers.Default) {
            repeat(1_000) { i ->
                launch {
                    sm.transitionTo(if (i % 2 == 0) SessionState.Listening else SessionState.Speaking)
                }
            }
        }

        val final = sm.state.value
        assertTrue(final is SessionState.Listening || final is SessionState.Speaking, "unexpected $final")
    }
}