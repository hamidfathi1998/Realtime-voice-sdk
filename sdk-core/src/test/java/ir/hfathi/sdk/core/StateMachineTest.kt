package ir.hfathi.sdk.core

import app.cash.turbine.test
import ir.hfathi.sdk.core.models.AudioFrame
import ir.hfathi.sdk.core.models.ClientEvent
import ir.hfathi.sdk.core.models.SessionState
import ir.hfathi.sdk.core.models.VoiceError
import ir.hfathi.sdk.core.statemachine.SessionStateMachine
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StateMachineTest {

    @Test
    fun `initial state is Idle`() = runTest {
        val stateMachine = SessionStateMachine()
        assertEquals(SessionState.Idle, stateMachine.state.value)
    }

    @Test
    fun `valid lifecycle transitions flow smoothly`() = runTest {
        val sm = SessionStateMachine()
        sm.state.test {
            assertEquals(SessionState.Idle, awaitItem())
            assertTrue(sm.transitionTo(SessionState.Connecting))
            assertEquals(SessionState.Connecting, awaitItem())
            assertTrue(sm.transitionTo(SessionState.Connected))
            assertEquals(SessionState.Connected, awaitItem())
            assertTrue(sm.transitionTo(SessionState.Listening))
            assertEquals(SessionState.Listening, awaitItem())
            assertTrue(sm.transitionTo(SessionState.Speaking))
            assertEquals(SessionState.Speaking, awaitItem())
            assertTrue(sm.transitionTo(SessionState.Disconnecting))
            assertEquals(SessionState.Disconnecting, awaitItem())
            assertTrue(sm.transitionTo(SessionState.Idle))
            assertEquals(SessionState.Idle, awaitItem())
        }
    }

    @Test
    fun `invalid transitions are rejected`() {
        val sm = SessionStateMachine()
        assertEquals(SessionState.Idle, sm.state.value)
        // Cannot jump directly from Idle to Speaking or Connected
        assertFalse(sm.transitionTo(SessionState.Speaking))
        assertFalse(sm.transitionTo(SessionState.Connected))
        assertEquals(SessionState.Idle, sm.state.value)
    }

    @Test
    fun `failure transition sets Failed state with VoiceError`() = runTest {
        val sm = SessionStateMachine()
        sm.state.test {
            assertEquals(SessionState.Idle, awaitItem())
            sm.transitionTo(SessionState.Connecting)
            assertEquals(SessionState.Connecting, awaitItem())
            val error = VoiceError.Network("Socket timeout", null)
            sm.fail(error)
            val failedState = awaitItem()
            assertTrue(failedState is SessionState.Failed)
            assertEquals("Socket timeout", (failedState as SessionState.Failed).error.message)
        }
    }

    @Test
    fun `events are emitted and observed through SharedFlow`() = runTest {
        val sm = SessionStateMachine()
        sm.events.test {
            val audioChunk = AudioFrame(byteArrayOf(0x01, 0x02, 0x03), 1000L, 24000, 1)
            sm.emitEvent(ClientEvent.ServerAudioChunk(audioChunk))
            val received = awaitItem()
            assertTrue(received is ClientEvent.ServerAudioChunk)
            assertEquals(audioChunk, received.frame)

            sm.emitEvent(ClientEvent.UserStartedSpeaking)
            assertEquals(ClientEvent.UserStartedSpeaking, awaitItem())

            sm.emitEvent(ClientEvent.Interrupted)
            assertEquals(ClientEvent.Interrupted, awaitItem())
        }
    }
}