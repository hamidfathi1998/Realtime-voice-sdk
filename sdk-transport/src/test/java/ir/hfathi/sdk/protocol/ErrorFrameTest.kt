package ir.hfathi.sdk.protocol

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ErrorFrameTest {

    @Test
    fun `error codes match the spec`() {
        assertEquals(1001, ErrorCode.INVALID_FRAME.code)
        assertEquals(1002, ErrorCode.CHECKSUM_MISMATCH.code)
        assertEquals(1003, ErrorCode.UNSUPPORTED_CODEC.code)
        assertEquals(1004, ErrorCode.AUTHENTICATION_FAILED.code)
        assertEquals(1005, ErrorCode.SESSION_NOT_FOUND.code)
        assertEquals(1006, ErrorCode.TRANSPORT_ERROR.code)
        assertEquals(1007, ErrorCode.TIMEOUT.code)
        assertEquals(1008, ErrorCode.RESOURCE_EXHAUSTED.code)
        assertEquals(9999, ErrorCode.UNKNOWN.code)
    }

    @Test
    fun `error codes are unique`() {
        val codes = ErrorCode.entries.map { it.code }
        assertEquals(codes.size, codes.toSet().size)
    }

    @Test
    fun `details are empty by default`() {
        val error = ErrorFrame(ErrorCode.TIMEOUT, "server timed out")
        assertTrue(error.details.isEmpty())
    }

    @Test
    fun `fields are kept as given`() {
        val details = mapOf("sequence" to "42")
        val error = ErrorFrame(ErrorCode.CHECKSUM_MISMATCH, "bad checksum", details)
        assertEquals(ErrorCode.CHECKSUM_MISMATCH, error.errorCode)
        assertEquals("bad checksum", error.message)
        assertEquals(details, error.details)
    }

    @Test
    fun `error frames with same values are equal`() {
        assertEquals(
            ErrorFrame(ErrorCode.TIMEOUT, "t", mapOf("a" to "b")),
            ErrorFrame(ErrorCode.TIMEOUT, "t", mapOf("a" to "b"))
        )
    }
}