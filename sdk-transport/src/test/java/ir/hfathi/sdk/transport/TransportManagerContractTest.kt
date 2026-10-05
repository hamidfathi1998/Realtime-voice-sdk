package ir.hfathi.sdk.transport

import io.mockk.mockk
import ir.hfathi.sdk.protocol.FrameType
import ir.hfathi.sdk.protocol.ProtocolFrame
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.ByteString
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

abstract class TransportManagerContractTest {

    abstract fun createTransport(): TransportManager

    private lateinit var server: MockWebServer
    private lateinit var transport: TransportManager
    private val receivedByServer = LinkedBlockingQueue<ByteString>()

    @BeforeEach
    fun setUp() {
        server = MockWebServer()
        server.enqueue(MockResponse().withWebSocketUpgrade(object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {}
            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                receivedByServer.add(bytes)
            }
        }))
        server.start()
        transport = createTransport()
    }

    @AfterEach
    fun tearDown() {
        transport.disconnect()
        server.shutdown()
    }

    private fun config() = WebSocketConfig(
        url = "ws://${server.hostName}:${server.port}",
        useSSL = false
    )

    private fun waitUntilConnected() {
        repeat(20) {
            if (transport.isConnected()) return
            Thread.sleep(100)
        }
    }

    @Test
    fun `not connected before connect`() {
        assertFalse(transport.isConnected())
    }

    @Test
    fun `connect succeeds and isConnected becomes true`() {
        assertTrue(transport.connect(config()).isSuccess)
        waitUntilConnected()
        assertTrue(transport.isConnected())
    }

    @Test
    fun `disconnect makes isConnected false`() {
        transport.connect(config())
        waitUntilConnected()
        assertTrue(transport.disconnect().isSuccess)
        assertFalse(transport.isConnected())
    }

    @Test
    fun `send fails when not connected`() {
        val frame = ProtocolFrame(FrameType.HEARTBEAT, 1u, 0L, ByteArray(0))
        assertTrue(transport.send(frame).isFailure)
    }

    @Test
    fun `send delivers frame bytes to the server`() {
        transport.connect(config())
        waitUntilConnected()
        val frame = ProtocolFrame(FrameType.AUDIO_DATA, 1u, 0L, byteArrayOf(1, 2, 3))

        assertTrue(transport.send(frame).isSuccess)

        val bytes = receivedByServer.poll(2, TimeUnit.SECONDS)
        assertNotNull(bytes)
        assertContentEquals(frame.toBytes(), bytes.toByteArray())
    }

    @Test
    fun `adding and removing a listener does not throw`() {
        val listener = mockk<TransportListener>(relaxed = true)
        transport.addMessageListener(listener)
        transport.removeMessageListener(listener)
    }
}