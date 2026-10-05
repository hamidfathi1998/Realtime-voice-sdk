package ir.hfathi.sdk.transport

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertTrue

abstract class LatencyOptimizerContractTest {

    abstract fun createOptimizer(): LatencyOptimizer

    private lateinit var optimizer: LatencyOptimizer

    @BeforeEach
    fun setUp() {
        optimizer = createOptimizer()
    }

    @Test
    fun `round trip time is never negative`() {
        assertTrue(optimizer.measureRoundTripTime() >= 0)
    }

    @Test
    fun `jitter is never negative`() {
        assertTrue(optimizer.measureJitter() >= 0)
    }

    // Spec: jitter buffer is 10-100ms
    @ParameterizedTest
    @ValueSource(ints = [10, 50, 100])
    fun `accepts buffer sizes within the 10 to 100ms range`(size: Int) {
        assertDoesNotThrow { optimizer.adjustBufferSize(size) }
    }

    @Test
    fun `suggestFrameDropping can be called before any measurement`() {
        assertDoesNotThrow { optimizer.suggestFrameDropping() }
    }
}