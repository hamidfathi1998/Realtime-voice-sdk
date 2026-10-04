package ir.hfathi.sdk.core

import ir.hfathi.sdk.core.models.AudioFrame
import ir.hfathi.sdk.core.models.ClientEvent
import ir.hfathi.sdk.core.models.SessionState
import ir.hfathi.sdk.core.models.VoiceError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class VoiceModelsTest {

    private val base = AudioFrame(byteArrayOf(1, 2, 3), timestampNs = 10L, sampleRate = 24000, channels = 1)

    @Test
    fun `AudioFrame equality compares content not array reference`() {
        val other = AudioFrame(byteArrayOf(1, 2, 3), 10L, 24000, 1)
        assertEquals(base, other)
        assertEquals(base.hashCode(), other.hashCode())
        assertEquals(base, base)
    }

    @Test
    fun `AudioFrame is not equal when any field differs`() {
        assertNotEquals(base, base.copy(data = byteArrayOf(9)))
        assertNotEquals(base, base.copy(timestampNs = 11L))
        assertNotEquals(base, base.copy(sampleRate = 16000))
        assertNotEquals(base, base.copy(channels = 2))
        assertNotEquals<Any?>(base, null)
        assertNotEquals<Any>(base, "not a frame")
    }

    @Test
    fun `AudioFrame defaults to 24kHz mono`() {
        val frame = AudioFrame(byteArrayOf())
        assertEquals(24000, frame.sampleRate)
        assertEquals(1, frame.channels)
    }

    @Test
    fun `every VoiceError subtype keeps message and cause`() {
        val root = IllegalStateException("root")
        val errors: List<VoiceError> = listOf(
            VoiceError.Network("net", root),
            VoiceError.Authentication("auth", root),
            VoiceError.AudioHardware("hw", root),
            VoiceError.Protocol("proto", root),
            VoiceError.Unknown("unknown", root),
        )
        assertEquals(listOf("net", "auth", "hw", "proto", "unknown"), errors.map { it.message })
        errors.forEach {
            assertSame(root, it.cause)
            assertIs<Exception>(it)
        }
    }

    @Test
    fun `VoiceError cause defaults to null`() {
        assertNull(VoiceError.Protocol("bad frame").cause)
    }

    @Test
    fun `Failed states with the same error instance are equal`() {
        val error = VoiceError.Network("timeout")
        assertEquals(SessionState.Failed(error), SessionState.Failed(error))
    }

    @Test
    fun `TranscriptReceived behaves as a value type`() {
        val a = ClientEvent.TranscriptReceived("hello", isFinal = true, isUser = false)
        val b = ClientEvent.TranscriptReceived("hello", isFinal = true, isUser = false)
        assertEquals(a, b)
        assertNotEquals(a, a.copy(isFinal = false))
    }
}