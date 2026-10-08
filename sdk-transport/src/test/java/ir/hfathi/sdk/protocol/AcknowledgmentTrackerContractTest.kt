package ir.hfathi.sdk.protocol


import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

abstract class AcknowledgmentTrackerContractTest {

    /** Return your AcknowledgmentTracker implementation here. */
    abstract fun createTracker(): AcknowledgmentTracker

    private lateinit var tracker: AcknowledgmentTracker

    @BeforeEach
    fun setUp() {
        tracker = createTracker()
    }

    @Test
    fun `no pending frames initially`() {
        assertTrue(tracker.getPendingFrames().isEmpty())
    }

    @Test
    fun `tracked frame is pending and not acknowledged`() {
        tracker.trackFrame(1u)
        assertFalse(tracker.isAcknowledged(1u))
        assertEquals(listOf(1u), tracker.getPendingFrames())
    }

    @Test
    fun `acknowledge marks frame and removes it from pending`() {
        tracker.trackFrame(1u)
        tracker.trackFrame(2u)

        assertTrue(tracker.acknowledge(1u).isSuccess)

        assertTrue(tracker.isAcknowledged(1u))
        assertEquals(listOf(2u), tracker.getPendingFrames())
    }

    @Test
    fun `acknowledging all frames empties pending list`() {
        tracker.trackFrame(1u)
        tracker.trackFrame(2u)
        tracker.acknowledge(1u)
        tracker.acknowledge(2u)
        assertTrue(tracker.getPendingFrames().isEmpty())
    }

    @Test
    fun `acknowledging an untracked frame fails`() {
        assertTrue(tracker.acknowledge(99u).isFailure)
    }

    @Test
    fun `untracked frame is not acknowledged`() {
        assertFalse(tracker.isAcknowledged(99u))
    }
}