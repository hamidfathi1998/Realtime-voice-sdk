package ir.hfathi.sdk.transport

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class WebSocketConfigTest {

    @Test
    fun `default values match the spec`() {
        val config = WebSocketConfig(url = "wss://ir.hfathi.voice")
        assertEquals(true, config.useSSL)
        assertEquals(10000L, config.connectionTimeoutMs)
        assertEquals(30000L, config.readTimeoutMs)
        assertEquals(30000L, config.writeTimeoutMs)
        assertEquals(5, config.maxReconnectAttempts)
        assertEquals(1000L, config.initialBackoffMs)
        assertEquals(32000L, config.maxBackoffMs)
    }

    @Test
    fun `custom values are kept`() {
        val config = WebSocketConfig(
            url = "ws://localhost:8080",
            useSSL = false,
            connectionTimeoutMs = 5000,
            maxReconnectAttempts = 3
        )
        assertEquals("ws://localhost:8080", config.url)
        assertEquals(false, config.useSSL)
        assertEquals(5000L, config.connectionTimeoutMs)
        assertEquals(3, config.maxReconnectAttempts)
    }

    @Test
    fun `configs with same values are equal`() {
        assertEquals(WebSocketConfig(url = "wss://a.com"), WebSocketConfig(url = "wss://a.com"))
    }

    @Test
    fun `copy changes only the given field`() {
        val original = WebSocketConfig(url = "wss://a.com")
        val copy = original.copy(maxBackoffMs = 60000)
        assertEquals(60000L, copy.maxBackoffMs)
        assertEquals(original.initialBackoffMs, copy.initialBackoffMs)
        assertNotEquals(original, copy)
    }
}