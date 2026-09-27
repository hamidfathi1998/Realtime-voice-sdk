package ir.hfathi.sdk.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

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