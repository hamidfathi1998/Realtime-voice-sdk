package ir.hfathi.sdk.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.junit5.JUnit5Asserter.assertTrue

class VoiceSdkCoreTest {

    @Test
    fun verifyCoreInitialization() {
        assertTrue("Core should report initialized", VoiceSdkCore.isInitialized())
    }

    @Test
    fun verifyVersionPresence() {
        assertEquals("0.1.0-alpha", VoiceSdkCore.VERSION)
    }
}