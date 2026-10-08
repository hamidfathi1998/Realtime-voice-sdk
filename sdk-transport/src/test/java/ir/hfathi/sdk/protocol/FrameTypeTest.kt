package ir.hfathi.sdk.protocol

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class FrameTypeTest {

    @Test
    fun `ids match the spec`() {
        assertEquals(0x01.toByte(), FrameType.AUDIO_DATA.id)
        assertEquals(0x02.toByte(), FrameType.CONTROL.id)
        assertEquals(0x03.toByte(), FrameType.HEARTBEAT.id)
        assertEquals(0x04.toByte(), FrameType.ACK.id)
        assertEquals(0x05.toByte(), FrameType.ERROR.id)
    }

    @Test
    fun `there are exactly five frame types`() {
        assertEquals(5, FrameType.entries.size)
    }

    @Test
    fun `ids are unique`() {
        assertEquals(FrameType.entries.size, FrameType.entries.map { it.id }.toSet().size)
    }
}