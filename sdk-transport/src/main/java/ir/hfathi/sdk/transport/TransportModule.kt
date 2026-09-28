package ir.hfathi.sdk.transport

import ir.hfathi.sdk.core.VoiceSdkCore

/**
 * Anchor class for Chapter 1 scaffold.
 * Transport and network protocol implementations will land in Chapter 3.
 */
class TransportModule {
    val isReady: Boolean = VoiceSdkCore.isInitialized()
}