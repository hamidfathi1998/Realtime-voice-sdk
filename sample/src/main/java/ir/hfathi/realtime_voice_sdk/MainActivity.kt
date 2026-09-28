package ir.hfathi.realtime_voice_sdk

import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.os.Bundle
import ir.hfathi.sdk.audio.AudioModule
import ir.hfathi.sdk.core.VoiceSdkCore
import ir.hfathi.sdk.transport.TransportModule


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val audio = AudioModule()
        val transport = TransportModule()

        val statusText = buildString {
            appendLine("=== Realtime Voice SDK Scaffold ===")
            appendLine("Core Version: ${VoiceSdkCore.VERSION}")
            appendLine("Core Init: ${VoiceSdkCore.isInitialized()}")
            appendLine("Audio Module Linked: ${audio.isReady}")
            appendLine("Transport Module Linked: ${transport.isReady}")
        }

        val textView = TextView(this).apply {
            text = statusText
            textSize = 18f
            setPadding(48, 96, 48, 48)
        }

        setContentView(textView)
    }
}