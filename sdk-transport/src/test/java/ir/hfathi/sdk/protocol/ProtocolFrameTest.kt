package ir.hfathi.sdk.protocol

import org.junit.jupiter.api.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ProtocolFrameTest {

    // 1 (type) + 4 (sequence) + 8 (timestamp) + 4 (payload length) + 4 (checksum)
    private val headerSize = 21

    private fun sampleFrame(payload: ByteArray = byteArrayOf(10, 20, 30)) = ProtocolFrame(
        frameType = FrameType.AUDIO_DATA,
        sequenceNumber = 42u,
        timestamp = 1_700_000_000_000L,
        payload = payload
    )

    private fun assertSameFrame(expected: ProtocolFrame, actual: ProtocolFrame) {
        assertEquals(expected.frameType, actual.frameType)
        assertEquals(expected.sequenceNumber, actual.sequenceNumber)
        assertEquals(expected.timestamp, actual.timestamp)
        assertEquals(expected.checksum, actual.checksum)
        assertContentEquals(expected.payload, actual.payload)
    }

    @Test
    fun `default checksum is calculated from payload`() {
        val payload = byteArrayOf(1, 2, 3)
        assertEquals(calculateChecksum(payload), sampleFrame(payload).checksum)
    }

    @Test
    fun `different payloads give different checksums`() {
        assertNotEquals(
            sampleFrame(byteArrayOf(1, 2, 3)).checksum,
            sampleFrame(byteArrayOf(1, 2, 4)).checksum
        )
    }

    @Test
    fun `toBytes size is header plus payload`() {
        assertEquals(headerSize + 3, sampleFrame().toBytes().size)
    }

    @Test
    fun `toBytes follows the binary layout in little endian`() {
        val frame = sampleFrame()
        val buffer = ByteBuffer.wrap(frame.toBytes()).order(ByteOrder.LITTLE_ENDIAN)

        assertEquals(FrameType.AUDIO_DATA.id, buffer.get())
        assertEquals(42u, buffer.int.toUInt())
        assertEquals(1_700_000_000_000L, buffer.long)
        assertEquals(3, buffer.int)
        assertEquals(frame.checksum, buffer.int.toUInt())
        val payload = ByteArray(3).also { buffer.get(it) }
        assertContentEquals(byteArrayOf(10, 20, 30), payload)
    }

    @Test
    fun `fromBytes of toBytes returns the same frame`() {
        val original = sampleFrame()
        val decoded = ProtocolFrame.fromBytes(original.toBytes()).getOrThrow()
        assertSameFrame(original, decoded)
    }

    @Test
    fun `round trip works for every frame type`() {
        FrameType.entries.forEach { type ->
            val original = ProtocolFrame(type, 1u, 0L, byteArrayOf(7))
            assertSameFrame(original, ProtocolFrame.fromBytes(original.toBytes()).getOrThrow())
        }
    }

    @Test
    fun `round trip works with empty payload`() {
        val original = sampleFrame(ByteArray(0))
        assertSameFrame(original, ProtocolFrame.fromBytes(original.toBytes()).getOrThrow())
    }

    @Test
    fun `round trip keeps max sequence number`() {
        val original = ProtocolFrame(FrameType.HEARTBEAT, UInt.MAX_VALUE, 0L, ByteArray(0))
        val decoded = ProtocolFrame.fromBytes(original.toBytes()).getOrThrow()
        assertEquals(UInt.MAX_VALUE, decoded.sequenceNumber)
    }

    @Test
    fun `fromBytes fails when data is shorter than header`() {
        assertTrue(ProtocolFrame.fromBytes(ByteArray(10)).isFailure)
    }

    @Test
    fun `fromBytes fails on empty data`() {
        assertTrue(ProtocolFrame.fromBytes(ByteArray(0)).isFailure)
    }

    @Test
    fun `fromBytes fails on unknown frame type`() {
        val bytes = sampleFrame().toBytes()
        bytes[0] = 0x7F
        assertTrue(ProtocolFrame.fromBytes(bytes).isFailure)
    }

    @Test
    fun `fromBytes fails when payload is truncated`() {
        val bytes = sampleFrame().toBytes().copyOf(headerSize + 1)
        assertTrue(ProtocolFrame.fromBytes(bytes).isFailure)
    }

    @Test
    fun `fromBytes fails when payload is corrupted`() {
        val bytes = sampleFrame().toBytes()
        bytes[bytes.lastIndex] = (bytes.last() + 1).toByte()
        assertTrue(ProtocolFrame.fromBytes(bytes).isFailure)
    }
}