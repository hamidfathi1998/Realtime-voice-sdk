package ir.hfathi.sdk.core

import app.cash.turbine.test
import ir.hfathi.sdk.core.models.AudioFrame
import ir.hfathi.sdk.core.models.ClientEvent
import ir.hfathi.sdk.core.models.SessionState
import ir.hfathi.sdk.core.statemachine.SessionStateMachine
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/** Test double only. Real implementation arrives in Chapter 5. */
private class FakeRealtimeVoiceClient(
    private val sm: SessionStateMachine = SessionStateMachine()
) : RealtimeVoiceClient {
    override val state: StateFlow<SessionState> = sm.state
    override val events: SharedFlow<ClientEvent> = sm.events
    val sentFrames = mutableListOf<AudioFrame>()

    override suspend fun startSession() {
        check(sm.transitionTo(SessionState.Connecting))
        check(sm.transitionTo(SessionState.Connected))
        check(sm.transitionTo(SessionState.Listening))
    }

    override suspend fun stopSession() {
        check(sm.transitionTo(SessionState.Disconnecting))
        check(sm.transitionTo(SessionState.Idle))
        sm.emitEvent(ClientEvent.Disconnected)
    }

    override suspend fun sendAudio(frame: AudioFrame) {
        val s = state.value
        check(s is SessionState.Listening || s is SessionState.Speaking) { "not streaming: $s" }
        sentFrames += frame
    }

    override suspend fun interrupt() {
        sm.emitEvent(ClientEvent.Interrupted)
        sm.transitionTo(SessionState.Listening)
    }

    fun simulateServerSpeaking() = sm.transitionTo(SessionState.Speaking)
}

class FakeRealtimeVoiceClientTest {

    @Test
    fun `full session lifecycle through the public contract`() = runTest {
        val client = FakeRealtimeVoiceClient()
        client.state.test {
            assertEquals(SessionState.Idle, awaitItem())
            client.startSession()
            assertEquals(SessionState.Connecting, awaitItem())
            assertEquals(SessionState.Connected, awaitItem())
            assertEquals(SessionState.Listening, awaitItem())
            client.stopSession()
            assertEquals(SessionState.Disconnecting, awaitItem())
            assertEquals(SessionState.Idle, awaitItem())
        }
    }

    @Test
    fun `interrupt while speaking emits Interrupted and returns to Listening`() = runTest {
        val client = FakeRealtimeVoiceClient()
        client.startSession()
        assertTrue(client.simulateServerSpeaking())
        client.events.test {
            client.interrupt()
            assertEquals(ClientEvent.Interrupted, awaitItem())
        }
        assertEquals(SessionState.Listening, client.state.value)
    }

    @Test
    fun `sendAudio is rejected before the session starts`() = runTest {
        val client = FakeRealtimeVoiceClient()
        assertFailsWith<IllegalStateException> {
            client.sendAudio(AudioFrame(byteArrayOf(1), 0L))
        }
    }

    @Test
    fun `sendAudio forwards frames while listening`() = runTest {
        val client = FakeRealtimeVoiceClient()
        client.startSession()
        val frame = AudioFrame(byteArrayOf(1, 2), 5L)
        client.sendAudio(frame)
        assertEquals(listOf(frame), client.sentFrames)
    }
}