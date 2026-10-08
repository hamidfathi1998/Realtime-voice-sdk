package ir.hfathi.sdk.protocol

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class AudioFrameTest {

    private fun frame(size: Int) =
        AudioFrame(samples = ShortArray(size), timestamp = 0L, sequenceNumber = 1u)

    @ParameterizedTest
    @ValueSource(ints = [80, 160, 320, 960])
    fun `accepts allowed frame sizes`(size: Int) {
        assertEquals(size, frame(size).samples.size)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 79, 159, 161, 480, 1000])
    fun `rejects invalid frame sizes`(size: Int) {
        assertThrows<IllegalArgumentException> { frame(size) }
    }

    @Test
    fun `error message lists allowed sizes`() {
        val error = assertThrows<IllegalArgumentException> { frame(100) }
        assertEquals("Audio frame size must be 80, 160, 320, or 960 samples", error.message)
    }

    @Test
    fun `default sample rate is 16kHz`() {
        assertEquals(16000, frame(160).sampleRate)
    }

    @Test
    fun `fields are kept as given`() {
        val samples = ShortArray(160) { it.toShort() }
        val frame = AudioFrame(samples, 48000, 123L, 7u)
        assertContentEquals(samples, frame.samples)
        assertEquals(48000, frame.sampleRate)
        assertEquals(123L, frame.timestamp)
        assertEquals(7u, frame.sequenceNumber)
    }
}