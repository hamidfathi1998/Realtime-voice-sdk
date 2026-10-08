package ir.hfathi.sdk.protocol

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class ControlMessageTest {

    @Test
    fun `control message codes match the spec`() {
        assertEquals(1, ControlMessageType.START_SESSION.code)
        assertEquals(2, ControlMessageType.END_SESSION.code)
        assertEquals(3, ControlMessageType.SET_CODEC.code)
        assertEquals(4, ControlMessageType.SET_SAMPLE_RATE.code)
        assertEquals(5, ControlMessageType.PAUSE.code)
        assertEquals(6, ControlMessageType.RESUME.code)
        assertEquals(7, ControlMessageType.MUTE.code)
        assertEquals(8, ControlMessageType.UNMUTE.code)
        assertEquals(9, ControlMessageType.REQUEST_STATS.code)
    }

    @Test
    fun `codes are unique`() {
        val codes = ControlMessageType.entries.map { it.code }
        assertEquals(codes.size, codes.toSet().size)
    }

    @Test
    fun `every message type has a ControlMessage subclass`() {
        assertEquals(
            ControlMessageType.entries.size,
            ControlMessage::class.sealedSubclasses.size
        )
    }

    @Test
    fun `messages with same values are equal`() {
        assertEquals(
            ControlMessage.StartSession("s1", "u1"),
            ControlMessage.StartSession("s1", "u1")
        )
    }

    @Test
    fun `messages with different values are not equal`() {
        assertNotEquals<ControlMessage>(
            ControlMessage.Pause("s1"),
            ControlMessage.Pause("s2")
        )
        assertNotEquals<ControlMessage>(
            ControlMessage.Mute("s1"),
            ControlMessage.Unmute("s1")
        )
    }

    @Test
    fun `fields are kept as given`() {
        val end = ControlMessage.EndSession("s1", "user hung up")
        assertEquals("s1", end.sessionId)
        assertEquals("user hung up", end.reason)
        assertEquals(48000, ControlMessage.SetSampleRate(48000).sampleRate)
        assertEquals("pcm", ControlMessage.SetCodec("pcm").codec)
    }
}