package ir.hfathi.sdk.protocol


import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SequenceManagerTest {

    @Test
    fun `first sequence number is 1`() {
        assertEquals(1u, SequenceManager().nextSequenceNumber())
    }

    @Test
    fun `sequence numbers increase by one`() {
        val manager = SequenceManager()
        assertEquals(listOf(1u, 2u, 3u, 4u, 5u), List(5) { manager.nextSequenceNumber() })
    }

    @Test
    fun `separate managers have independent counters`() {
        val a = SequenceManager()
        val b = SequenceManager()
        a.nextSequenceNumber()
        a.nextSequenceNumber()
        assertEquals(1u, b.nextSequenceNumber())
    }

    @Test
    fun `validate accepts in-order sequence numbers`() {
        val manager = SequenceManager()
        assertTrue(manager.validate(1u).isSuccess)
        assertTrue(manager.validate(2u).isSuccess)
        assertTrue(manager.validate(3u).isSuccess)
    }

    @Test
    fun `validate rejects duplicate sequence number`() {
        val manager = SequenceManager()
        manager.validate(5u)
        assertTrue(manager.validate(5u).isFailure)
    }

    @Test
    fun `validate rejects older sequence number`() {
        val manager = SequenceManager()
        manager.validate(5u)
        assertTrue(manager.validate(3u).isFailure)
    }
}