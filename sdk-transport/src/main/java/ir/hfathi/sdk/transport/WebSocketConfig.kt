package ir.hfathi.sdk.transport

data class WebSocketConfig(
    val url: String,
    val useSSL: Boolean = true,
    val connectionTimeoutMs: Long = 10000,
    val readTimeoutMs: Long = 30000,
    val writeTimeoutMs: Long = 30000,
    val maxReconnectAttempts: Int = 5,
    val initialBackoffMs: Long = 1000,
    val maxBackoffMs: Long = 32000
)