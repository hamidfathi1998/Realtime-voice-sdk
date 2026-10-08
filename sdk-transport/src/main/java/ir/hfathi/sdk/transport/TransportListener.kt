package ir.hfathi.sdk.transport

import ir.hfathi.sdk.protocol.ErrorFrame
import ir.hfathi.sdk.protocol.ProtocolFrame

interface TransportListener {
    fun onFrameReceived(frame: ProtocolFrame)
    fun onConnected()
    fun onDisconnected(reason: String)
    fun onError(error: ErrorFrame)
}