package ir.hfathi.sdk.transport

import ir.hfathi.sdk.protocol.ProtocolFrame

interface TransportManager {
    fun connect(config: WebSocketConfig): Result<Unit>
    fun disconnect(): Result<Unit>
    fun send(frame: ProtocolFrame): Result<Unit>
    fun addMessageListener(listener: TransportListener): Unit
    fun removeMessageListener(listener: TransportListener): Unit
    fun isConnected(): Boolean
}