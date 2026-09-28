package ir.hfathi.sdk.audio

import ir.hfathi.sdk.core.VoiceSdkCore


/**
 * Anchor class for Chapter 1 scaffold.
 * Audio capture/playback implementations will land in Chapter 4.
 */
class AudioModule {
    val isReady: Boolean = VoiceSdkCore.isInitialized()
}